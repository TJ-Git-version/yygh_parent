package top.forforever.yygh.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.sms.service.SmsService;

/**
 * @create: 2023/3/19
 * @Description:
 * @FileName: SmsController
 * @自定义内容：
 */
@RestController
@RequestMapping("/user/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send/{phone}")
    public R send(@PathVariable String phone){
        boolean flag = smsService.sendCode(phone);
        return flag ? R.ok() : R.error();
    }

}
