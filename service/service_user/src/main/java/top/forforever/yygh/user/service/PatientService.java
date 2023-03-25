package top.forforever.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.forforever.yygh.model.user.Patient;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author forever
 * @since 2023-03-21
 */
public interface PatientService extends IService<Patient> {

    void insert(Patient patient, Long userId);

    List<Patient> getPatientList(String token);

    Patient detail(Long patientId);

    List<Patient> selectPatientList(Long userid);

}
