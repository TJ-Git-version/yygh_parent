package top.forforever.yygh.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.vo.order.OrderCountQueryVo;
import top.forforever.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author forever
 * @since 2023-03-22
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, Long patientId);

    Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo, Long userId);

    OrderInfo detail(Long orderId);

    void cancelOrder(Long orderId);

    void patientRemind();

    Map<String, Object> getStaCountMap(OrderCountQueryVo orderCountQueryVo);

}
