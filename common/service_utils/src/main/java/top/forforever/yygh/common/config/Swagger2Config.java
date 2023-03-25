package top.forforever.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @create: 2023/3/5
 * @Description:
 * @FileName: Swagger2Config
 * @自定义内容：
 */
@SpringBootConfiguration
@EnableSwagger2 //开启swagger注解支持
public class Swagger2Config {

    @Bean
    public Docket getAdminDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")//指定组名，分组
                .apiInfo(getAdminApiInfo())//api中文信息
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*"))) //指定哪些接口路径加入当前组
                .build();
    }

    public ApiInfo getAdminApiInfo(){
        return new ApiInfoBuilder()
                .title("管理员系统")
                .description("尚医通医院挂号平台之管理员系统")
                .version("1.0")
                //.contact(new Contact("xiaomu","","2929408642@qq.com"))
                .build();
    }

    @Bean
    public Docket getUserDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .apiInfo(getUserApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }

    public ApiInfo getUserApiInfo(){
        return new ApiInfoBuilder()
                .title("普通用户系统")
                .description("尚医通医院挂号平台之普通用户系统")
                .version("1.0")
                //.contact(new Contact("xiaomu","","2929408642@qq.com"))
                .build();
    }

    @Bean
    public Docket getApiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(getApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    public ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("第三方医院对接系统")
                .description("尚医通医院挂号平台之第三方医院对接系统")
                .version("1.0")
                //.contact(new Contact("xiaomu","","2929408642@qq.com"))
                .build();
    }

}
