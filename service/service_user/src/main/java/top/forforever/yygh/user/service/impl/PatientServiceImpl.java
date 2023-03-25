package top.forforever.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import top.forforever.yygh.client.DictFeignClient;
import top.forforever.yygh.model.user.Patient;
import top.forforever.yygh.user.mapper.PatientMapper;
import top.forforever.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.forforever.yygh.common.uitl.AuthContextHolder;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author forever
 * @since 2023-03-21
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void insert(Patient patient, Long userId) {
        patient.setUserId(userId);
        baseMapper.insert(patient);
    }

    @Override
    public List<Patient> getPatientList(String token) {
        Long userId = AuthContextHolder.getUserId(token);
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getUserId,userId);
        List<Patient> patients = baseMapper.selectList(queryWrapper);
        patients.forEach(this::packPatient);
        return patients;
    }

    @Override
    public Patient detail(Long patientId) {
        Patient patient = baseMapper.selectById(patientId);
        this.packPatient(patient);
        return patient;
    }

    @Override
    public List<Patient> selectPatientList(Long userid) {
        List<Patient> patients = baseMapper.selectList(new LambdaQueryWrapper<Patient>().eq(Patient::getUserId, userid));
        patients.forEach(this::packPatient);
        return patients;
    }

    private void packPatient(Patient patient) {
        patient.getParam().put("certificatesTypeString",dictFeignClient.getNameByValue(Long.parseLong(patient.getCertificatesType())));
        String provinceString = dictFeignClient.getNameByValue(Long.parseLong(patient.getProvinceCode()));
        String cityString = dictFeignClient.getNameByValue(Long.parseLong(patient.getCityCode()));
        String districtString = dictFeignClient.getNameByValue(Long.parseLong(patient.getDistrictCode()));
        patient.getParam().put("provinceString",provinceString);
        patient.getParam().put("cityString",cityString);
        patient.getParam().put("districtString",districtString);
        patient.getParam().put("fullAddress",provinceString+cityString+districtString+patient.getAddress());
    }
}
