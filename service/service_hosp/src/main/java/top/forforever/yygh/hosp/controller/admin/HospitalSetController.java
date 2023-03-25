package top.forforever.yygh.hosp.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.common.uitl.MD5;
import top.forforever.yygh.hosp.service.HospitalSetService;
import top.forforever.yygh.model.hosp.HospitalSet;
import top.forforever.yygh.vo.hosp.HospitalQueryVo;
import top.forforever.yygh.vo.hosp.HospitalSetQueryVo;

import javax.print.attribute.standard.JobHoldUntil;
import javax.xml.crypto.Data;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author forever
 * @since 2023-03-05
 */
@Api(tags = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Slf4j
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("锁定与解锁")
    @PutMapping("/status/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        //HospitalSet hospitalSet = hospitalSetService.getById(id); //乐观锁：一般要并发量高的
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @ApiOperation("批量删除")
    @DeleteMapping
    public R batchDelete(@RequestBody List<Integer> ids){
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation(value = "修改之回显数据")
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Integer id){
        return R.ok().data("item",hospitalSetService.getById(id));
    }

    @ApiOperation(value = "修改之修改数据")
    @PutMapping("/update")
    public R update(@RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @ApiOperation(value = "新增医院设置")
    @PostMapping("/save")
    public R save(@RequestBody HospitalSet hospitalSet){
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名秘钥：时间戳+随机数+MD5加密
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+ random.nextInt(1000)));
        hospitalSetService.save(hospitalSet);
        return R.ok();
    }

    @ApiOperation(value = "根据条件查询分页信息")
    @PostMapping("/page/{pageNum}/{pageSize}")
    public R getPageInfo(@PathVariable Integer pageNum,
                         @PathVariable Integer pageSize,
                         @RequestBody HospitalSetQueryVo hospitalSetQueryVo){
        return hospitalSetService.getPageList(pageNum,pageSize,hospitalSetQueryVo);
    }

    @ApiOperation(value = "查询所有医院设置列表")
    @GetMapping("/findAll")
    public R findAll(){
        log.info("current thread is "+Thread.currentThread().getId());

        List<HospitalSet> list = hospitalSetService.list();

        log.info("result "+Thread.currentThread().getId()+list.toString());
        return R.ok().data("items",list);
    }

    //根据医院id删除医院设置信息
    @ApiOperation(value = "根据医院id删除医院设置信息")
    @DeleteMapping("/deleteById/{id}")
    public R deleteById(@PathVariable String id){
        hospitalSetService.removeById(id);
        return R.ok();
    }
}

