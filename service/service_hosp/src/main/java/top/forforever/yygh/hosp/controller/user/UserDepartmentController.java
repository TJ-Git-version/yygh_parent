package top.forforever.yygh.hosp.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.service.DepartmentService;
import top.forforever.yygh.vo.hosp.DepartmentVo;

import java.util.List;

/**
 * @create: 2023/3/19
 * @Description:
 * @FileName: UserDepartmentController
 * @自定义内容：
 */
@RestController
@RequestMapping("/user/hosp/department")
public class UserDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/all/{hoscode}")
    public R getDepartmentList(@PathVariable String hoscode){
        List<DepartmentVo> departmentVoList = departmentService.getDepartmentVoList(hoscode);
        return R.ok().data("list",departmentVoList);
    }

}
