package top.forforever.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.enums.OrderStatusEnum;
import top.forforever.yygh.enums.PaymentStatusEnum;
import top.forforever.yygh.enums.PaymentTypeEnum;
import top.forforever.yygh.enums.RefundStatusEnum;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.model.order.PaymentInfo;
import top.forforever.yygh.model.order.RefundInfo;
import top.forforever.yygh.order.prop.WeiPayProperties;
import top.forforever.yygh.order.service.OrderInfoService;
import top.forforever.yygh.order.service.PaymentService;
import top.forforever.yygh.order.service.RefundInfoService;
import top.forforever.yygh.order.service.WeiPayService;
import top.forforever.yygh.order.utils.ConstantPropertiesUtils;
import top.forforever.yygh.order.utils.HttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: WeiPayServiceImpl
 * @自定义内容：
 */
@Service
@Transactional
public class WeiPayServiceImpl implements WeiPayService {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WeiPayProperties weiPayProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public String createNative(Long orderId) {

        String redisUrl = (String) redisTemplate.opsForValue().get(orderId.toString());
        if (!StringUtils.isEmpty(redisUrl)) return redisUrl;

        //1.根据订单id去数据库中获取订单信息
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        //2.保存支付记录信息
        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        //3.请求微信服务器获取微信支付的url地址
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weiPayProperties.getAppid());//公众账号id
        paramMap.put("mch_id",weiPayProperties.getPartner());//商户号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//获取随机字符串

        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊"+ orderInfo.getDepname();
        paramMap.put("body",body);//商品描述

        paramMap.put("out_trade_no",orderInfo.getOutTradeNo());//订单号
        paramMap.put("total_fee","1");//商品金额 分为单位
        paramMap.put("spbill_create_ip","127.0.0.1"); //终端IP
        paramMap.put("notify_url","https://forforever.top");//通知地址
        paramMap.put("trade_type","NATIVE");//交易类型

        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weiPayProperties.getPartnerkey()));//设置参数
            httpClient.setHttps(true);//支持https协议
            httpClient.post();//发送post请求

            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            String codeUrl = map.get("code_url");
            if (!StringUtils.isEmpty(codeUrl)) {
                //微信支付二维码2小时过期，可采取2小时未支付取消订单
                redisTemplate.opsForValue().set(orderId.toString(),codeUrl,2, TimeUnit.HOURS);
            }

            //4.将url返回给前端
            return codeUrl;
        }catch (Exception ex){
           return "";
        }
    }

    @Override
    public Map<String, String> getPayStatus(Long orderId) {
        OrderInfo orderInfo = orderInfoService.getById(orderId);

        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weiPayProperties.getAppid());
        paramMap.put("mch_id",weiPayProperties.getPartner());
        paramMap.put("out_trade_no",orderInfo.getOutTradeNo());
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());

        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap,weiPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.post();

            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(httpClient.getContent());
            return xmlToMap;
        }catch (Exception ex) {
            return null;
        }

    }

    @Override
    public void paySuccess(Long orderId, Map<String, String> map) {
        //更新订单表中的状态
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
        //修改支付表中的支付状态
        LambdaUpdateWrapper<PaymentInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(!StringUtils.isEmpty(orderId),PaymentInfo::getOrderId,orderId);
        updateWrapper.set(PaymentInfo::getTradeNo,map.get("transaction_id"))//微信支付订单号【微信服务器生成的】
                     .set(PaymentInfo::getPaymentStatus,PaymentStatusEnum.PAID.getStatus())
                     .set(PaymentInfo::getCallbackTime,new Date())
                     .set(PaymentInfo::getCallbackContent, JSONObject.toJSONString(map));
        paymentService.update(updateWrapper);
    }

    @Override
    public boolean refund(Long orderId) {
        PaymentInfo paymentInfo = paymentService.getOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, orderId));

        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
        if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
            return true;
        }

        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
        Map<String,String> paramMap = new HashMap<>(8);
        paramMap.put("appid", weiPayProperties.getAppid());       //公众账号ID
        paramMap.put("mch_id",weiPayProperties.getPartner());   //商户编号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
        paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
        paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
        //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee","1");
        paramMap.put("refund_fee","1");
        try {
            String paramXml = WXPayUtil.generateSignedXml(paramMap,weiPayProperties.getPartnerkey());
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.setCert(true);
            httpClient.setCertPassword(weiPayProperties.getPartner());//设置证书密码
            httpClient.post();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(httpClient.getContent());
            if (resultMap != null && "SUCCESS".equals(resultMap.get("result_code"))) {
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackTime(new Date());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
