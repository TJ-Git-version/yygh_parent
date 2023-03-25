package top.forforever.yygh.hosp.controller.admin;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.model.acl.User;

/**
 * @create: 2023/3/9
 * @Description:
 * @FileName: UserController
 * @自定义内容：
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @PostMapping("/login")
    public R login(@RequestBody User user){
        if (StringUtils.isEmpty(user.getUsername())){
            throw new YyghException(20001,"用户名不能为空");
        }
        if (StringUtils.isEmpty(user.getPassword())){
            throw new YyghException(20001,"密码不能为空");
        }
        //"token":"admin-token"
        return R.ok().data("token","admin-token");
    }

    @GetMapping("/info")
    public R info(){
        return R.ok().data("roles","[admin]")
                .data("introduction","I am a super administrator")
                .data("avatar","https://si1.go2yd.com/get-image/0t0ZKTlA6bk")
                .data("name","Super Admin");
    }


}
