package top.forforever.yygh.order.service;

import java.util.Map;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: WeiPayService
 * @自定义内容：
 */
public interface WeiPayService {
    String createNative(Long orderId);

    Map<String, String> getPayStatus(Long orderId);

    void paySuccess(Long orderId, Map<String, String> map);

    boolean refund(Long orderId);

}
