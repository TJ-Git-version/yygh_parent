package top.forforever.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import top.forforever.yygh.enums.PaymentStatusEnum;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.model.order.PaymentInfo;
import top.forforever.yygh.order.mapper.PaymentMapper;
import top.forforever.yygh.order.service.PaymentService;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {

        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOrderId,order.getId())
                    .eq(PaymentInfo::getPaymentType,paymentType);

        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            return;
        }

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setTotalAmount(order.getAmount());

        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);

        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());

        baseMapper.insert(paymentInfo);

    }
}