package top.forforever.yygh.task.job;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.forforever.yygh.rabbit.service.RabbitService;
import top.forforever.yygh.rabbit.utils.MqConst;

/**
 * @create: 2023/3/25
 * @Description:
 * @FileName: PatientRemindJob
 * @自定义内容：
 */
@Component
public class PatientRemindJob {

    @Autowired
    private RabbitService rabbitService;

    /*
    在springboot定时任务使用：
        1.在主动类加 @EnableScheduling
        2.在定时任务Job方法上加 @Scheduled并指定石英表达式
        cron表达式写法：七域表达式

        Quarts:cron表达式： 秒   分   时   dayOfMonth  Month  dayOfWeek  Year[最高到2099年]
        * ：表示任意xxx
        ? ：表示无所谓
        - ：连续的时间段
        /n ：表示每隔多长时间
        , ：可以使用 , 隔开没有规律的时间
     */
    @Scheduled(cron = "* * 7 * * ?")
    public void patientSmsRemind() {
        //System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        //发送给service_order微服务，发送今天就诊的消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }

}
