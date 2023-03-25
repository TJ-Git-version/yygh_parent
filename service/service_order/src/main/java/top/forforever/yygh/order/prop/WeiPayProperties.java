package top.forforever.yygh.order.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @create: 2023/3/23
 * @Description:
 * @FileName: WeiPayProperties
 * @自定义内容：
 */
@ConfigurationProperties(value = "weipay")
@PropertySource(value = "classpath:weipay.properties")
@Component
@Data
public class WeiPayProperties {

    private String appid;
    private String partner;
    private String partnerkey;

}
