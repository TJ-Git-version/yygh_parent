package top.forforever.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.uitl.JwtHelper;
import top.forforever.yygh.enums.AuthStatusEnum;
import top.forforever.yygh.enums.StatusEnum;
import top.forforever.yygh.model.user.Patient;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.user.mapper.UserInfoMapper;
import top.forforever.yygh.user.service.PatientService;
import top.forforever.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.forforever.yygh.vo.user.LoginVo;
import top.forforever.yygh.vo.user.UserAuthVo;
import top.forforever.yygh.vo.user.UserInfoQueryVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author forever
 * @since 2023-03-19
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1.先获取用户输入的手机号和验证码信息
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //2.对接收到的手机号和验证码做一个非空验证
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(20001,"手机号或验证码有误");
        }
        //3.对验证码进一步确认
        String redisCode = (String) redisTemplate.opsForValue().get(phone);
        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode)){
            throw new YyghException(20001,"验证码错误");
        }

        UserInfo userInfo = null;
        //判断是否是微信登录
        if (StringUtils.isEmpty(loginVo.getOpenid())){
            //4.判断手机号首次登录，如果是首次登录，就先往表中注册一下当前用户信息
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getPhone,phone);
            userInfo = baseMapper.selectOne(queryWrapper);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }else {
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getOpenid,loginVo.getOpenid());
            userInfo = baseMapper.selectOne(queryWrapper);

            LambdaQueryWrapper<UserInfo> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(UserInfo::getPhone,phone);
            UserInfo userInfoByPhone = baseMapper.selectOne(phoneWrapper);
            if (userInfoByPhone == null){
                userInfo.setPhone(phone);
                baseMapper.updateById(userInfo);
            }else {
                userInfoByPhone.setOpenid(userInfo.getOpenid());
                userInfoByPhone.setNickName(userInfo.getNickName());
                //修改手机号存在的信息，把微信登录的信息修改进去
                baseMapper.updateById(userInfoByPhone);
                //删除微信登录的信息
                baseMapper.deleteById(userInfo.getId());
            }

        }
        //5.验证用户的status
        if (userInfo.getStatus() == 0) {
            throw new YyghException(20001,"用户锁定中~");
        }
        //6.返回用户信息
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);

        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);
        return map;
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return userInfo;
    }

    @Override
    public void updateUserInfo(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo != null){
            userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
            BeanUtils.copyProperties(userAuthVo,userInfo);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Page<UserInfo> getUserInfoPage(Integer pageNum, Integer pageSize, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(userInfoQueryVo.getKeyword()),UserInfo::getName,userInfoQueryVo.getKeyword())
                    .or().like(!StringUtils.isEmpty(userInfoQueryVo.getKeyword()),UserInfo::getPhone,userInfoQueryVo.getKeyword())
                    .or().eq(!StringUtils.isEmpty(userInfoQueryVo.getStatus()),UserInfo::getStatus,userInfoQueryVo.getStatus())
                    .or().gt(!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeBegin()),UserInfo::getCreateTime,userInfoQueryVo.getCreateTimeBegin())
                    .or().lt(!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeEnd()),UserInfo::getCreateTime,userInfoQueryVo.getCreateTimeEnd())
                    .or().eq(!StringUtils.isEmpty(userInfoQueryVo.getAuthStatus()),UserInfo::getAuthStatus,userInfoQueryVo.getAuthStatus());

        page = baseMapper.selectPage(page,queryWrapper);
        page.getRecords().forEach(this::packageUserInfo);
        return page;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == 0 || status == 1){
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> detail(Long userid) {
        UserInfo userInfo = baseMapper.selectById(userid);
        List<Patient> patients = patientService.selectPatientList(userid);
        Map<String,Object> map = new HashMap<>(2);
        map.put("userInfo",userInfo);
        map.put("patients",patients);
        return map;
    }

    private void packageUserInfo(UserInfo userInfo) {
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        userInfo.getParam().put("statusString", StatusEnum.getStatusStringByStatus(userInfo.getStatus()));
    }
}
