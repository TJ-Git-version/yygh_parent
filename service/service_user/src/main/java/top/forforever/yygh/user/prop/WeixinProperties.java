package top.forforever.yygh.user.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @create: 2023/3/20
 * @Description:
 * @FileName: WeixinProperties
 * @自定义内容：
 */
@Data
@ConfigurationProperties(prefix = "weixin")
public class WeixinProperties {
    private String appId;
    private String appSecret;
    private String redirectUri;
}
