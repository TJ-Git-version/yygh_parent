package top.forforever.yygh.hosp.util;

import org.springframework.util.StringUtils;
import top.forforever.yygh.common.uitl.MD5;
import top.forforever.yygh.hosp.service.HospitalSetService;

import java.util.Map;

/**
 * @create: 2023/3/14
 * @Description:
 * @FileName: commonMethod
 * @自定义内容：
 */
public class CommonMethod {
    //signkey验证
    public static boolean verifySignKey(Map<String, Object> switchMap, HospitalSetService hospitalSetService){
        String resultSignKey = (String) switchMap.get("sign");
        String resultHoscode = (String) switchMap.get("hoscode");
        String signkey = hospitalSetService.getSignkeyByHoscode(resultHoscode);
        String encrypt = MD5.encrypt(signkey);
        if (!StringUtils.isEmpty(resultSignKey) && !StringUtils.isEmpty(encrypt) && encrypt.equals(resultSignKey)){
            return true;
        }
        return false;
    }
}
