package top.forforever.yygh.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.forforever.yygh.model.user.Patient;

/**
 * @create: 2023/3/22
 * @Description:
 * @FileName: UserFeignClient
 * @自定义内容：
 */
@FeignClient("service-user")
public interface PatientFeignClient {
    @GetMapping("/user/userinfo/patient/{patientId}")
    public Patient getPatient(@PathVariable("patientId") Long patientId);
}
