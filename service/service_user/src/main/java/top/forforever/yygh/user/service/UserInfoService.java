package top.forforever.yygh.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.vo.user.LoginVo;
import top.forforever.yygh.vo.user.UserAuthVo;
import top.forforever.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author forever
 * @since 2023-03-19
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo getUserInfo(Long userId);

    void updateUserInfo(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> getUserInfoPage(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo);

    void updateStatus(Long id, Integer status);

    Map<String, Object> detail(Long id);
}
