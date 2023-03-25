package top.forforever.yygh.hosp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.hosp.hepler.HttpRequestHepler;
import top.forforever.yygh.hosp.result.Result;
import top.forforever.yygh.hosp.service.HospitalSetService;
import top.forforever.yygh.hosp.service.ScheduleService;
import top.forforever.yygh.hosp.util.CommonMethod;
import top.forforever.yygh.model.hosp.Schedule;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @create: 2023/3/14
 * @Description:
 * @FileName: ScheduleController
 * @自定义内容：
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @PostMapping("/schedule/list")
    public Result<Page> getSchedulePage(HttpServletRequest request){
        Map<String, Object> switchMap = HttpRequestHepler.switchMap(request.getParameterMap());
        //signkey验证
        if (CommonMethod.verifySignKey(switchMap,hospitalSetService)){
           Page<Schedule> schedulePage = scheduleService.getSchedulePage(switchMap);
           return Result.ok(schedulePage);
        }else {
            throw new YyghException(20001,"查询排班信息失败");
        }

    }

    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> switchMap = HttpRequestHepler.switchMap(request.getParameterMap());
        String hosScheduleId = (String) switchMap.get("hosScheduleId");
        String depcode = (String) switchMap.get("depcode");
        //signkey验证
        if (CommonMethod.verifySignKey(switchMap,hospitalSetService)
                && !StringUtils.isEmpty(hosScheduleId) && !StringUtils.isEmpty(depcode)){

            scheduleService.saveSchedule(switchMap);
            return Result.ok();
        }else {
            throw new YyghException(20001,"保存排班信息失败");
        }
    }

    @PostMapping("/schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> switchMap = HttpRequestHepler.switchMap(request.getParameterMap());
        //验证signkey
        if (CommonMethod.verifySignKey(switchMap,hospitalSetService)){
            scheduleService.remove(switchMap);
            return Result.ok();
        }else {
            throw new YyghException(20001,"删除排班信息失败！");
        }
    }
}
