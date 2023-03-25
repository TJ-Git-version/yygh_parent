package top.forforever.yygh.hosp.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.hosp.service.HospitalService;
import top.forforever.yygh.model.hosp.Hospital;
import top.forforever.yygh.vo.hosp.HospitalQueryVo;

/**
 * @create: 2023/3/14
 * @Description:
 * @FileName: HospitalController
 * @自定义内容：
 */
@RestController
@RequestMapping("/admin/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //根据医院id获取医院详情
    @GetMapping("/{id}")
    public R getHospById(@PathVariable String id){
        Hospital hospital = hospitalService.getHospById(id);
        return R.ok().data("hospital",hospital);
    }

    //根据医院id修改医院状态
    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public R getHospitalPage(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize,
                             HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(pageNum,pageSize,hospitalQueryVo);
        return R.ok().data("total",hospitalPage.getTotalElements()).data("list",hospitalPage.getContent());
    }

}
