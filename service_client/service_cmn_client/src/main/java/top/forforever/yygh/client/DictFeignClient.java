package top.forforever.yygh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @create: 2023/3/15
 * @Description:
 * @FileName: DictFeignClent
 * @自定义内容：
 */
@FeignClient(value = "service-cmn")
public interface DictFeignClient {
    //根据医院所属的省市区编号获取省市区名称
    @GetMapping("/admin/cmn/{value}")
    public String getNameByValue(@PathVariable("value") Long value);

    //根据医院的等级编号获取医院等级名称
    @GetMapping("/admin/cmn/{dictcode}/{value}")
    public String getNameByDictCodeAndValue(@PathVariable("dictcode") String dictcode,
                                            @PathVariable("value") Long value);
}
