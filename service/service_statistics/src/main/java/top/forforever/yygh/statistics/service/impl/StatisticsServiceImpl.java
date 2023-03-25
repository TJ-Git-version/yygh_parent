package top.forforever.yygh.statistics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.forforever.yygh.order.client.OrderFeignClient;
import top.forforever.yygh.statistics.service.StatisticsService;
import top.forforever.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @create: 2023/3/25
 * @Description:
 * @FileName: StatisticsServiceImpl
 * @自定义内容：
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Override
    public Map<String, Object> getStaCountMap(OrderCountQueryVo orderCountQueryVo) {
        return orderFeignClient.getStaCountMap(orderCountQueryVo);
    }
}
