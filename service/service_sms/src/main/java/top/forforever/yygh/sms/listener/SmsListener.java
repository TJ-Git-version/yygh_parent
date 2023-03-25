package top.forforever.yygh.sms.listener;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.forforever.yygh.rabbit.utils.MqConst;
import top.forforever.yygh.sms.service.SmsService;
import top.forforever.yygh.vo.msm.MsmVo;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: SmsListener
 * @自定义内容：
 */
@Component
public class SmsListener {

    @Autowired
    private SmsService smsService;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(name = MqConst.QUEUE_SMS_ITEM,durable = "true"),
                            exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_SMS),
                            key = MqConst.ROUTING_SMS_ITEM
                    )
            }
    )
    public void consumer(MsmVo msmVo){
        smsService.sendMessage(msmVo);
    }
}
