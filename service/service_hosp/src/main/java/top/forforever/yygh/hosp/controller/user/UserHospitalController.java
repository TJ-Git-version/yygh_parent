package top.forforever.yygh.hosp.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.service.HospitalService;
import top.forforever.yygh.model.hosp.Hospital;
import top.forforever.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;

/**
 * @create: 2023/3/19
 * @Description:
 * @FileName: UserHospitalController
 * @自定义内容：
 */
@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/list")
    public R getHospitalList(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> page = hospitalService.getHospitalPage(1, 1000, hospitalQueryVo);
        return R.ok().data("list",page.getContent());
    }

    //不建议：controller接口复用
    @GetMapping("/{hosname}")
    public R findByNameLike(@PathVariable String hosname){
        List<Hospital> list = hospitalService.findByHosnameLike(hosname);
        return R.ok().data("list",list);
    }

    @GetMapping("/detail/{hoscode}")
    public R getHospitalDetail(@PathVariable String hoscode){
        Hospital hospital = hospitalService.getHospitalDetail(hoscode);
        return R.ok().data("hospital",hospital);
    }

}
