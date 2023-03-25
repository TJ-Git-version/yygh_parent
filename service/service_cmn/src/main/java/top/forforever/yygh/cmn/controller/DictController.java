package top.forforever.yygh.cmn.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import top.forforever.yygh.cmn.service.DictService;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author forever
 * @since 2023-03-10
 */
@RestController
@RequestMapping("/admin/cmn")
public class DictController {

    @Autowired
    private DictService dictService;

    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {
        dictService.upload(file);
        return R.ok();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        dictService.download(response);
    }

    @GetMapping("/childList/{pid}")
    public R getChildListByPid(@PathVariable Long pid){
       List<Dict> list = dictService.getChildListByPid(pid);
       return R.ok().data("items",list);
    }

    @DeleteMapping("/delete/{id}")
    public R deleteById(@PathVariable String id) {
        dictService.deleteById(id);
        return R.ok();
    }

    //根据医院所属的省市区编号获取省市区名称
    @GetMapping("/{value}")
    public String getNameByValue(@PathVariable("value") Long value){
        return dictService.getNameByDictCodeAndValue("",value);
    }

    //根据医院的等级编号获取医院等级名称
    @GetMapping("/{dictcode}/{value}")
    public String getNameByDictCodeAndValue(@PathVariable("dictcode") String dictcode,
                                           @PathVariable("value") Long value){
        return dictService.getNameByDictCodeAndValue(dictcode,value);
    }

}

