package top.forforever.yygh.rabbit.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: MQConfig
 * @自定义内容：
 */
@Configuration
public class MQConfig {

    /*
        作用：就是将发送到RabbitMQ中的pojo对象自动就转换为json格式存储
             从rabbitmq消费消息时，自动把json格式的字符串转换为pojo对象
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}


