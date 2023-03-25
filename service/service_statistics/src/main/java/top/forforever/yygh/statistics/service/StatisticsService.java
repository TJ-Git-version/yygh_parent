package top.forforever.yygh.statistics.service;

import top.forforever.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @create: 2023/3/25
 * @Description:
 * @FileName: StatisticsService
 * @自定义内容：
 */
public interface StatisticsService {

    Map<String, Object> getStaCountMap(OrderCountQueryVo orderCountQueryVo);

}
