package top.forforever.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @create: 2023/3/5
 * @Description:
 * @FileName: ServiceHospMainStarter
 * @自定义内容：
 */
@SpringBootApplication
@ComponentScan(value = "top.forforever.yygh") //这里扫描的是包中含有top.forforever.yygh路径的注解，开发规范所有模块都以相同包名起
@MapperScan(value = "top.forforever.yygh.hosp.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "top.forforever.yygh")
public class ServiceHospMainStarter {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMainStarter.class,args);
    }
}
