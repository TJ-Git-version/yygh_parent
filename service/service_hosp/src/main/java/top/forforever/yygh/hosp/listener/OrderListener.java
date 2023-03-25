package top.forforever.yygh.hosp.listener;

import com.rabbitmq.client.Channel;
import io.lettuce.core.dynamic.annotation.Key;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.forforever.yygh.hosp.service.ScheduleService;
import top.forforever.yygh.rabbit.service.RabbitService;
import top.forforever.yygh.rabbit.utils.MqConst;
import top.forforever.yygh.vo.msm.MsmVo;
import top.forforever.yygh.vo.order.OrderMqVo;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: OrderListener
 * @自定义内容：
 */
@Component
public class OrderListener {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(name = MqConst.QUEUE_ORDER,durable = "true"),//创建队列
                            exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER),//创建交换机
                            key = MqConst.ROUTING_ORDER //绑定路由key
                    )
            }
    )
    public void consumer(OrderMqVo orderMqVo, Message message, Channel channel  ){
        //调用schedule方法减可预约数
        MsmVo msmVo = orderMqVo.getMsmVo();
        if (orderMqVo.getAvailableNumber() != null) {
            scheduleService.updateAvailableNumber(orderMqVo.getScheduleId(),orderMqVo.getAvailableNumber());
        }else {
            scheduleService.cancelSchedule(orderMqVo);
        }
        if (msmVo != null){
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        }
    }

}
