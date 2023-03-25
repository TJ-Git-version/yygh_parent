package top.forforever.yygh.user.controller.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import top.forforever.yygh.common.result.R;
import top.forforever.yygh.common.uitl.JwtHelper;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.user.service.UserInfoService;
import top.forforever.yygh.common.uitl.AuthContextHolder;
import top.forforever.yygh.vo.user.LoginVo;
import top.forforever.yygh.vo.user.UserAuthVo;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author forever
 * @since 2023-03-19
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PutMapping("/update")
    public R update(@RequestHeader String token, UserAuthVo userAuthVo){
        Long userId = AuthContextHolder.getUserId(token);
        userInfoService.updateUserInfo(userId,userAuthVo);
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        Map<String,Object> map = userInfoService.login(loginVo);
        return R.ok().data(map);
    }

    @GetMapping("/info")
    public R getUserInfo(@RequestHeader String token){
        UserInfo userInfo = null;
        if (!StringUtils.isEmpty(token)){
            Long userId = JwtHelper.getUserId(token);
            userInfo = userInfoService.getUserInfo(userId);
        }
        return R.ok().data("userInfo",userInfo);
    }

}

