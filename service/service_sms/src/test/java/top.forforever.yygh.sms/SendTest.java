package top.forforever.yygh.sms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.sms.service.SmsService;

/**
 * @create: 2023/3/19
 * @Description:
 * @FileName: SendTest
 * @自定义内容：
 */
@SpringBootTest
public class SendTest {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testSend(){//18038997590
        smsService.sendCode("15625699553");
    }

    @Test
    public void testString(){
        redisTemplate.opsForValue().set("测试","12345");
    }
}
