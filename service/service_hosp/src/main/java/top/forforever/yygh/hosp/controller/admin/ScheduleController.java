package top.forforever.yygh.hosp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.service.ScheduleService;

import java.util.Map;

/**
 * @create: 2023/3/18
 * @Description:
 * @FileName: ScheduleController
 * @自定义内容：
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    //根据医院编号和科室编号查询排班时间
    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getSchedulePage(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize,
                             @PathVariable String hoscode,
                             @PathVariable String depcode){
        Map<String,Object> map = scheduleService.getPageByHoscodeAndDepcode(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }

    @GetMapping("/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workDate){

        return R.ok().data("list",scheduleService.getScheduleDetail(hoscode,depcode,workDate));
    }

}
