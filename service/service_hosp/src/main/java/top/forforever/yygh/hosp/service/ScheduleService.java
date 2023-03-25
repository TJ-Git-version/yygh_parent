package top.forforever.yygh.hosp.service;

import org.springframework.data.domain.Page;
import top.forforever.yygh.model.hosp.Schedule;
import top.forforever.yygh.vo.hosp.ScheduleOrderVo;
import top.forforever.yygh.vo.order.OrderMqVo;

import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/14
 * @Description:
 * @FileName: ScheduleService
 * @自定义内容：
 */
public interface ScheduleService {
    void saveSchedule(Map<String, Object> switchMap);

    Page<Schedule> getSchedulePage(Map<String, Object> switchMap);

    void remove(Map<String, Object> switchMap);

    Map<String, Object> getPageByHoscodeAndDepcode(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String, Object> getUserSchedulePage(String hoscode, String depcode, Integer pageNum, Integer pageSize);

    Schedule getScheduleById(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    void updateAvailableNumber(String scheduleId, Integer availableNumber);

    void cancelSchedule(OrderMqVo orderMqVo);
}
