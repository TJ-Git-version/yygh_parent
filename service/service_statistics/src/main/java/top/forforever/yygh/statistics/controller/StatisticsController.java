package top.forforever.yygh.statistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.statistics.service.StatisticsService;
import top.forforever.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @create: 2023/3/25
 * @Description:
 * @FileName: StatisticsController
 * @自定义内容：
 */
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/getStaCountMap")
    public R getStaCountMap(OrderCountQueryVo orderCountQueryVo){
        Map<String,Object> map = statisticsService.getStaCountMap(orderCountQueryVo);
        return R.ok().data(map);
    }

}
