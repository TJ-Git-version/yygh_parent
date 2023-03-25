package top.forforever.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.forforever.yygh.enums.PaymentTypeEnum;
import top.forforever.yygh.enums.RefundStatusEnum;
import top.forforever.yygh.model.order.PaymentInfo;
import top.forforever.yygh.model.order.RefundInfo;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.order.mapper.RefundInfoMapper;
import top.forforever.yygh.order.service.RefundInfoService;

/**
 * @create: 2023/3/24
 * @Description:
 * @FileName: RefundInfoServiceImpl
 * @自定义内容：
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundInfo::getOrderId,paymentInfo.getOrderId())
                    .eq(RefundInfo::getPaymentType,paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(queryWrapper);
        //防止用户打开两个网址一个订单新增两个记录
        if (refundInfo != null) {
            return refundInfo;
        }
        refundInfo = new RefundInfo();
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject("取消预约，想退款...");
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }

    @Override
    public RefundInfo isRefund(Long orderId) {
        LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundInfo::getOrderId,orderId);
        return baseMapper.selectOne(queryWrapper);
    }
}
