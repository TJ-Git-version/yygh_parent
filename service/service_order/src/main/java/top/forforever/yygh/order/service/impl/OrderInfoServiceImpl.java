package top.forforever.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.uitl.AuthContextHolder;
import top.forforever.yygh.enums.OrderStatusEnum;
import top.forforever.yygh.enums.PaymentStatusEnum;
import top.forforever.yygh.enums.PaymentTypeEnum;
import top.forforever.yygh.hosp.client.ScheduleFeignClient;
import top.forforever.yygh.model.acl.User;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.model.order.PaymentInfo;
import top.forforever.yygh.model.order.RefundInfo;
import top.forforever.yygh.model.user.Patient;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.order.mapper.OrderInfoMapper;
import top.forforever.yygh.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.forforever.yygh.order.service.PaymentService;
import top.forforever.yygh.order.service.RefundInfoService;
import top.forforever.yygh.order.service.WeiPayService;
import top.forforever.yygh.rabbit.service.RabbitService;
import top.forforever.yygh.rabbit.utils.MqConst;
import top.forforever.yygh.user.client.PatientFeignClient;
import top.forforever.yygh.vo.hosp.ScheduleOrderVo;
import top.forforever.yygh.order.utils.HttpRequestHelper;
import top.forforever.yygh.vo.msm.MsmVo;
import top.forforever.yygh.vo.order.OrderCountQueryVo;
import top.forforever.yygh.vo.order.OrderCountVo;
import top.forforever.yygh.vo.order.OrderMqVo;
import top.forforever.yygh.vo.order.OrderQueryVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author forever
 * @since 2023-03-22
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ScheduleFeignClient scheduleFeignClient;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private WeiPayService weiPayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundInfoService refundInfoService;

    //生成订单
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //1.先根据scheduleId获取医生排班信息
        ScheduleOrderVo scheduleOrderVo = scheduleFeignClient.getScheduleOrderVo(scheduleId);
        if (new DateTime(scheduleOrderVo.getStopTime()).isBeforeNow()){
            throw new YyghException(20001,"超过了预约挂号时间");
        }
        //2.先根据patientId获取就诊人信息
        Patient patient = patientFeignClient.getPatient(patientId);

        //3.从平台请求第三方医院，确认当前用户能否挂号

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",scheduleOrderVo.getHoscode());
        paramMap.put("depcode",scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
        Date date = scheduleOrderVo.getReserveDate();

        paramMap.put("reserveDate",new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime",scheduleOrderVo.getReserveTime());
        paramMap.put("amount",scheduleOrderVo.getAmount());
        paramMap.put("quitTime",scheduleOrderVo.getQuitTime());

        JSONObject response = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");

        if (response != null && response.getIntValue("code") == 200 ){
            JSONObject data = response.getJSONObject("data");

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setOutTradeNo(System.currentTimeMillis()+""+new Random().nextInt(100));

            BeanUtils.copyProperties(scheduleOrderVo,orderInfo);

            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());


            orderInfo.setPatientId(patient.getId());
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setHosRecordId(data.getString("hosRecordId"));
            orderInfo.setNumber(data.getIntValue("number"));
            orderInfo.setFetchTime(data.getString("fetchTime"));
            orderInfo.setFetchAddress(data.getString("fetchAddress"));
            orderInfo.setAmount(scheduleOrderVo.getAmount());
            orderInfo.setQuitTime(scheduleOrderVo.getQuitTime());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
            //3.2 如果返回能挂号，就把医生排班信息、就诊人信息及第三方医院返回的信息都添加到order_info表中
            baseMapper.insert(orderInfo);

            //3.3 更新平台上对应医生的剩余可预约数
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setAvailableNumber(data.getIntValue("availableNumber"));
            orderMqVo.setReservedNumber(data.getIntValue("reservedNumber"));
            //3.4 给就诊人发送短信提醒
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);

            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);

            //4.返回订单的id
            return orderInfo.getId();
        }else {
            //3.1 如果返回不能挂号，直接抛出异常
            throw new YyghException(20001,"号源已满");
        }

    }

    @Override
    public Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo, Long userId) {
        Page<OrderInfo> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!StringUtils.isEmpty(userId),OrderInfo::getUserId,userId) //用户id
                    .eq(!StringUtils.isEmpty(orderQueryVo.getOutTradeNo()),OrderInfo::getOutTradeNo,orderQueryVo.getOutTradeNo()) //订单号
                    .eq(!StringUtils.isEmpty(orderQueryVo.getPatientId()),OrderInfo::getPatientId,orderQueryVo.getPatientId()) //就诊人id
                    .like(!StringUtils.isEmpty(orderQueryVo.getPatientName()),OrderInfo::getPatientName,orderQueryVo.getPatientName()) //就诊人姓名
                    .like(!StringUtils.isEmpty(orderQueryVo.getKeyword()),OrderInfo::getHosname,orderQueryVo.getKeyword()) //医院名称
                    .eq(!StringUtils.isEmpty(orderQueryVo.getOrderStatus()),OrderInfo::getOrderStatus,orderQueryVo.getOrderStatus()) //订单状态
                    .ge(!StringUtils.isEmpty(orderQueryVo.getReserveDate()),OrderInfo::getReserveDate,orderQueryVo.getReserveDate()) //预约时间
                    .ge(!StringUtils.isEmpty(orderQueryVo.getCreateTimeBegin()),OrderInfo::getCreateTime,orderQueryVo.getCreateTimeBegin()) //订单创建时间
                    .le(!StringUtils.isEmpty(orderQueryVo.getCreateTimeEnd()),OrderInfo::getCreateTime,orderQueryVo.getCreateTimeEnd()); //订单创建时间
        queryWrapper.orderByDesc(OrderInfo::getReserveDate);
        page = baseMapper.selectPage(page, queryWrapper);
        page.getRecords().forEach(this::packageOrderInfo);
        return page;
    }

    @Override
    public OrderInfo detail(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        orderInfo.getFetchTime();
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long orderId) {

        //1.确定当前取消预约的时间 和 挂号订单取消的时间 对比，当前时间是否已经超过了 挂号订单的取消预约截止时间：
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime dateTime = new DateTime(orderInfo.getQuitTime());

        //如果超过了，直接抛出异常，不让用户取消
        if (dateTime.isBeforeNow()) {
            throw new YyghException(20001, "已经超过预约截止时间，取消失败");
        }

        //判断用户是否已退款了，还继续发请求
        RefundInfo refundInfo = refundInfoService.isRefund(orderId);
        if (refundInfo != null) {
            throw new YyghException(20001,"您已在北京时间 "+new DateTime(refundInfo.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss")+" 时完成退款！请刷新当前页面！");
        }

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("hosRecordId",orderInfo.getHosRecordId());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        paramMap.put("sign", "");

        //2.从平台请求第三方医院，通知第三方医院，该医院已取消 但是也会存在其他情况：
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/updateCancelStatus");

        /*2.1 第三方医院如果不同意取消：抛出异常，不能取消 例：
            用户在挂号截止时间前一天 15:29分 点击了，刚好发送是31分，超过了时间，医院不同意取消医院 用户不能取消！
         */
        if (jsonObject == null || jsonObject.getIntValue("code") != 200) {
            throw new YyghException(20001,"第三方医院拒绝取消预约，请联系第三方医院查明原因！");
        }

        //3.判断用户是否对当前挂号订单是否已支付
        if (OrderStatusEnum.PAID.getStatus().equals(orderInfo.getOrderStatus())) {
            //3.1 如果已支付，退款
           boolean flag = weiPayService.refund(orderId);
           if (!flag) {
               throw new YyghException(20001,"退款失败，请联系管理员查明原因！");
           }
        }

        //不管支不支付，都要继续往下执行
        //4.1更新订单的订单状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);

        //4.2更新支付记录表的支付状态
        LambdaUpdateWrapper<PaymentInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaymentInfo::getOrderId,orderId)
                     .eq(PaymentInfo::getPaymentType, PaymentTypeEnum.WEIXIN.getStatus());
        updateWrapper.set(PaymentInfo::getPaymentStatus, PaymentStatusEnum.REFUND.getStatus());
        paymentService.update(updateWrapper);

        //5.更新医生的剩余可预约数信息 【预约数 + 1】
        //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());

        //6.给就诊人发送短信提示
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        orderMqVo.setMsmVo(msmVo);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);

    }

    @Override
    public void patientRemind() {
        //查询今天的就诊人消息 早上7点发送给就诊人短信 今天就诊
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getReserveDate,new DateTime().toString("yyyy-MM-dd"))
                    .ne(OrderInfo::getOrderStatus,OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfos = baseMapper.selectList(queryWrapper);
        orderInfos.forEach(orderInfo -> {
            //告诉service_sms 发送消息
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        });
    }

    @Override
    public Map<String, Object> getStaCountMap(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> orderCountVos = baseMapper.getStaCountMap(orderCountQueryVo);
        List<String> dateList = orderCountVos.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        List<Integer> countList = orderCountVos.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    private void packageOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString",OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
    }
}
