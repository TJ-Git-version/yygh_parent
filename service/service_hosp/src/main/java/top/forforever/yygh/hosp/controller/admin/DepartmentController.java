package top.forforever.yygh.hosp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.service.DepartmentService;
import top.forforever.yygh.model.hosp.Department;
import top.forforever.yygh.vo.hosp.DepartmentVo;

import java.util.List;

/**
 * @create: 2023/3/18
 * @Description:
 * @FileName: DepartmentController
 * @自定义内容：
 */
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hoscode}")
    public R getDepartmentVoList(@PathVariable String hoscode){
        List<DepartmentVo> departmentVos = departmentService.getDepartmentVoList(hoscode);
        return R.ok().data("list",departmentVos);
    }


}
