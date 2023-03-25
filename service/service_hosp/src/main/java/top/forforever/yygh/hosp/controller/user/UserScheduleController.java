package top.forforever.yygh.hosp.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.service.ScheduleService;
import top.forforever.yygh.model.hosp.Schedule;
import top.forforever.yygh.vo.hosp.ScheduleOrderVo;
import top.forforever.yygh.vo.hosp.ScheduleQueryVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/21
 * @Description:
 * @FileName: UserScheduleController
 * @自定义内容：
 */
@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    //根据医生的排班id查询医生信息
    @GetMapping("/info/{scheduleId}")
    public R getScheduleById(@PathVariable String scheduleId){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return R.ok().data("schedule",schedule);
    }

    @GetMapping("/{hoscode}/{depcode}/{pageNum}/{pageSize}")
    public R getUserSchedulePage(@PathVariable String hoscode,
                                 @PathVariable String depcode,
                                 @PathVariable Integer pageNum,
                                 @PathVariable Integer pageSize){

        Map<String,Object> map = scheduleService.getUserSchedulePage(hoscode,depcode,pageNum,pageSize);
        return R.ok().data(map);
    }

    @GetMapping("/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail( @PathVariable String hoscode,
                                @PathVariable String depcode,
                                @PathVariable String workDate){
        List<Schedule> details = scheduleService.getScheduleDetail(hoscode, depcode, workDate);
        return R.ok().data("details",details);
    }

    @GetMapping("/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId){
        return scheduleService.getScheduleOrderVo(scheduleId);
    }
}
