package top.forforever.yygh.cmn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @create: 2023/3/10
 * @Description:
 * @FileName: ServiceCmnApplication
 * @自定义内容：
 */
@SpringBootApplication
@ComponentScan("top.forforever.yygh")
@MapperScan("top.forforever.yygh.cmn.mapper")
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class);
    }
}
