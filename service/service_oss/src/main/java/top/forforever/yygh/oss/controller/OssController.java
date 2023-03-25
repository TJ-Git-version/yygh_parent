package top.forforever.yygh.oss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.oss.service.OssService;

/**
 * @create: 2023/3/20
 * @Description:
 * @FileName: OssController
 * @自定义内容：
 */
@RestController
@RequestMapping("/user/oss/file")
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public R upload(MultipartFile file){
        String url =  ossService.upload(file);
        return R.ok().data("url",url);
    }

}
