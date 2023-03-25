package top.forforever.yygh.hosp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.uitl.MD5;
import top.forforever.yygh.hosp.hepler.HttpRequestHepler;
import top.forforever.yygh.hosp.result.Result;
import top.forforever.yygh.hosp.service.DepartmentService;
import top.forforever.yygh.hosp.service.HospitalSetService;
import top.forforever.yygh.hosp.util.CommonMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @create: 2023/3/13
 * @Description:
 * @FileName: DepartmentController
 * @自定义内容：
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询所有科室信息
    @PostMapping("/department/list")
    public Result<Page> getDepartmentPage(HttpServletRequest request){
        Map<String, Object> switchMap = HttpRequestHepler.switchMap(request.getParameterMap());
        //signKey验证
        if (CommonMethod.verifySignKey(switchMap,hospitalSetService)){
            return Result.ok(departmentService.getDepartmentPage(switchMap));
        }else {
            throw new YyghException(20001,"查询失败");
        }
    }

    //新增科室信息
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, Object> switchMap = HttpRequestHepler.switchMap(request.getParameterMap());
        //signKey验证
        if (CommonMethod.verifySignKey(switchMap,hospitalSetService)){
            departmentService.saveDepartment(switchMap);
            return Result.ok();
        }else {
            return Result.fail().message("保存失败，请检查科室信息是否正常");
        }
    }

    //删除科室信息
    @PostMapping("/department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> switchMap = HttpRequestHepler.switchMap(request.getParameterMap());
        //signkey验证
        String depcode = (String) switchMap.get("depcode");
        String resultHoscode = (String) switchMap.get("hoscode");

        if (CommonMethod.verifySignKey(switchMap,hospitalSetService) && !StringUtils.isEmpty(depcode)){
            departmentService.remove(resultHoscode,depcode);
            return Result.ok();
        }else {
            throw new YyghException(20001,"删除失败");
        }
    }
}
