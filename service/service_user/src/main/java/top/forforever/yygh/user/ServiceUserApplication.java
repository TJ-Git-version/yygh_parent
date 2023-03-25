package top.forforever.yygh.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import top.forforever.yygh.user.prop.WeixinProperties;

@SpringBootApplication
@ComponentScan(basePackages = "top.forforever")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "top.forforever")
@MapperScan("top.forforever.yygh.user.mapper")
@EnableConfigurationProperties(value = WeixinProperties.class)
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}