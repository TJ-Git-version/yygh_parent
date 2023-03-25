package top.forforever.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.forforever.yygh.model.order.OrderInfo;
import top.forforever.yygh.vo.order.OrderCountQueryVo;
import top.forforever.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author forever
 * @since 2023-03-22
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> getStaCountMap(OrderCountQueryVo orderCountQueryVo);

}
