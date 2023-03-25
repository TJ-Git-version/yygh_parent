package top.forforever.yygh.user.controller.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import top.forforever.yygh.common.result.R;
import top.forforever.yygh.model.user.Patient;
import top.forforever.yygh.user.service.PatientService;
import top.forforever.yygh.common.uitl.AuthContextHolder;

import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author forever
 * @since 2023-03-21
 */
@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    //增
    @PostMapping("/save")
    public R save(@RequestBody Patient patient, @RequestHeader String token){
        if (!StringUtils.isEmpty(token)){
            Long userId = AuthContextHolder.getUserId(token);
            patientService.insert(patient,userId);
        }
        return R.ok();
    }

    //删
    @DeleteMapping("/delete/{patientId}")
    public R delete(@PathVariable Long patientId){
        patientService.removeById(patientId);
        return R.ok();
    }

    //1.修改之回显
    @GetMapping("/detail/{patientId}")
    public R detail(@PathVariable Long patientId){
        Patient patient = patientService.detail(patientId);
        return R.ok().data("patient",patient);
    }

    //2.修改之更新
    @PutMapping("/update")
    public R update(@RequestBody Patient patient){
        patientService.updateById(patient);
        return R.ok();
    }
    //查
    @GetMapping("/getPatientList")
    public R getPatientList(@RequestHeader String token){
        List<Patient> list = null;
        if (!StringUtils.isEmpty(token)){
            list = patientService.getPatientList(token);
        }

        return R.ok().data("list",list);
    }

    @GetMapping("/{patientId}")
    public Patient getPatient(@PathVariable("patientId") Long patientId){
        return patientService.getById(patientId);
    }

}

