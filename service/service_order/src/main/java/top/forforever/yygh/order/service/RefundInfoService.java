package top.forforever.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.forforever.yygh.model.order.PaymentInfo;
import top.forforever.yygh.model.order.RefundInfo;

//RefundInfoService
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);

    RefundInfo isRefund(Long orderId);

}