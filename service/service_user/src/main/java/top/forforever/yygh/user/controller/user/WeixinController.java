package top.forforever.yygh.user.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.forforever.yygh.common.exception.YyghException;
import top.forforever.yygh.common.result.R;
import top.forforever.yygh.common.uitl.JwtHelper;
import top.forforever.yygh.model.user.UserInfo;
import top.forforever.yygh.user.prop.WeixinProperties;
import top.forforever.yygh.user.service.UserInfoService;
import top.forforever.yygh.user.utils.HttpClientUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

/**
 * @create: 2023/3/20
 * @Description:
 * @FileName: WeixinController
 * @自定义内容：
 */
@Controller
@RequestMapping("/user/userinfo/wx")
public class WeixinController {

    @Autowired
    private WeixinProperties weixinProperties;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/param")
    @ResponseBody
    public R getWeixinLoginParams() throws UnsupportedEncodingException {
        String uri = URLEncoder.encode(weixinProperties.getRedirectUri(), "UTF-8");
        Map<String,Object> map = new HashMap<>();
        map.put("appId",weixinProperties.getAppId());
        map.put("scope","snsapi_login");
        map.put("redirectUri",uri);
        map.put("state",System.currentTimeMillis()+"");
        return R.ok().data(map);
    }

    @GetMapping("/callback")
    public String callback(String code,String state) throws Exception {
        StringBuilder append = new StringBuilder().append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String format = String.format(append.toString(), weixinProperties.getAppId(), weixinProperties.getAppSecret(), code);
        String result = HttpClientUtils.get(format);

        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject);
        //access_token访问微信服务器的一个凭证
        String access_token = jsonObject.getString("access_token");
        //openid是微信扫描用户在微信服务器的唯一标识
        String openid = jsonObject.getString("openid");

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getOpenid,openid);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if (userInfo == null){//第一次登录，注册用户
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);
            //获取微信用户信息，要再次发送请求获取
            StringBuilder builder = new StringBuilder().append("https://api.weixin.qq.com/sns/userinfo?")
                    .append("access_token=%s")
                    .append("&openid=%s");
            String resultUserinfo = HttpClientUtils.get(String.format(builder.toString(), access_token, openid));
            JSONObject jsonUserinfo = JSONObject.parseObject(resultUserinfo);
            String nickname = jsonUserinfo.getString("nickname");
            String sex = jsonUserinfo.getString("sex");
            String city = jsonUserinfo.getString("city");
            String headimgurl = jsonUserinfo.getString("headimgurl");
            userInfo.setNickName(nickname);
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }
        //已注册
        if (userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户锁定中~ 请联系管理员");
        }


        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        Map<String,String> map = new HashMap<>();
        //检查这个用户手机号是否为空：为空，说明这是首次使用微信登录，强制绑定手机号
        if (StringUtils.isEmpty(userInfo.getPhone())){
            map.put("openid",openid);
        }else {//检查这个用户手机号是否为空：不为空，说明这不是首次微信登录
            map.put("openid","");
        }
        map.put("name",name);
        //生成唯一token认证
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());
        map.put("token",token);
        return  "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
    }

}
