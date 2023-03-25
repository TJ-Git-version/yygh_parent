package top.forforever.yygh.sms.service;

import top.forforever.yygh.vo.msm.MsmVo;

/**
 * @create: 2023/3/19
 * @Description:
 * @FileName: SmsService
 * @自定义内容：
 */
public interface SmsService {
    boolean sendCode(String phone);

    void sendMessage(MsmVo msmVo);
}
