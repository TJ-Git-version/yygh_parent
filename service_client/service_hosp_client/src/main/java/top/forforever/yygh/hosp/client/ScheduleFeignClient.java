package top.forforever.yygh.hosp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.forforever.yygh.vo.hosp.ScheduleOrderVo;

/**
 * @create: 2023/3/22
 * @Description:
 * @FileName: HospFeignClient
 * @自定义内容：
 */
@FeignClient(value = "service-hosp")
public interface ScheduleFeignClient {

    @GetMapping("/user/hosp/schedule/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);

}
