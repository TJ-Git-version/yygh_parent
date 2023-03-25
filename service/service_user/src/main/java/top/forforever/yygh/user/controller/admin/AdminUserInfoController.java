package top.forforever.yygh.user.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.model.acl.User;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.user.service.UserInfoService;
import top.forforever.yygh.vo.user.UserInfoQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @create: 2023/3/21
 * @Description:
 * @FileName: AdminUserInfoController
 * @自定义内容：
 */
@RestController
@RequestMapping("/administrator/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PutMapping("/auth/{id}/{authStatus}")
    public R updateAuthStatus(@PathVariable Long id,@PathVariable Integer authStatus){
        if (authStatus == 2 || authStatus == -1){
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setAuthStatus(authStatus);
            userInfoService.updateById(userInfo);
        }
        return R.ok();
    }

    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        userInfoService.updateStatus(id,status);
        return R.ok();
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public R getUserInfoPage(@PathVariable Integer pageNum,
                  @PathVariable Integer pageSize,
                  UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> page = userInfoService.getUserInfoPage(pageNum,pageSize,userInfoQueryVo);
        return R.ok().data("total",page.getTotal()).data("list",page.getRecords());
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        Map<String,Object> map = userInfoService.detail(id);
        return R.ok().data(map);
    }

}
