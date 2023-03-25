package top.forforever.yygh.hosp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.uitl.MD5;
import top.forforever.yygh.hosp.hepler.HttpRequestHepler;
import top.forforever.yygh.hosp.result.Result;
import top.forforever.yygh.hosp.service.HospitalService;
import top.forforever.yygh.hosp.service.HospitalSetService;
import top.forforever.yygh.hosp.util.CommonMethod;
import top.forforever.yygh.model.hosp.Hospital;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @create: 2023/3/12
 * @Description:
 * @FileName: HospitalController
 * @自定义内容：
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询第三方医院信息
    @PostMapping("/hospital/show")
    public Result<Hospital> hospitalShow(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHepler.switchMap(request.getParameterMap());
        String hoscode = (String) map.get("hoscode");
        //signKey验证
        if (CommonMethod.verifySignKey(map,hospitalSetService)){
            Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
            return Result.ok(hospital);
        }else {
            throw new YyghException(20001,"查询失败！");
        }
    }

    //添加第三方医院信息
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> resultMap = HttpRequestHepler.switchMap(request.getParameterMap());
        if (CommonMethod.verifySignKey(resultMap,hospitalSetService)) {
            String logoData = (String) resultMap.get("logoData");
            resultMap.put("logoData", logoData.replaceAll(" ", "+"));
            hospitalService.saveHospital(resultMap);
            return Result.ok();
        } else {
            throw new YyghException(20001, "保存失败！");
        }
    }
}
