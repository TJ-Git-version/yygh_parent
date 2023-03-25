package top.forforever.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import top.forforever.yygh.model.hosp.Schedule;

import java.util.Date;
import java.util.List;

/**
 * @create: 2023/3/14
 * @Description:
 * @FileName: ScheduleRespository
 * @自定义内容：
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule findByHoscodeAndDepcodeAndHosScheduleId(String hoscode, String depcode, String hosScheduleId);

    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date date);

    Schedule findByHosScheduleId(String scheduleId);

}
