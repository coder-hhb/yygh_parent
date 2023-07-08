package com.hhb.yygh.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.model.user.UserInfo;
import com.hhb.yygh.user.prop.WeixinProperties;
import com.hhb.yygh.user.service.UserInfoService;
import com.hhb.yygh.user.utils.HttpClientUtils;
import com.hhb.yygh.user.utils.JwtHelper;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/userinfo/wx")
public class WxController {

    @Autowired
    private WeixinProperties weixinProperties;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/param")
    @ResponseBody
    public R getParam() throws UnsupportedEncodingException {
        //进行编码
        String redirectUri = URLEncoder.encode(weixinProperties.getRedirecturl(), "UTF-8");
        Map<String,Object> map = new HashMap<>();
        map.put("appid",weixinProperties.getAppid());
        map.put("redirecturi", redirectUri);
        map.put("scope","snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
        return R.ok().data(map);
    }

    @GetMapping("/callback")
    public String callback(String code,String state){
        System.out.println(code + ":" + state);
        StringBuilder stringBuilder = new StringBuilder();
        //设置新地址
        StringBuilder append = stringBuilder.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String format = String.format(append.toString()
                , weixinProperties.getAppid()
                , weixinProperties.getAppsecret()
                , code);
        try {
            String accesstokenInfo = HttpClientUtils.get(format);
            System.out.println(accesstokenInfo);
            //转化为jsonObject
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            //获取openid
            String openid = jsonObject.getString("openid");
            String access_token = jsonObject.getString("access_token");
            //根据openid查询是否有该微信用户信息
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid",openid);
            UserInfo userInfo = userInfoService.getOne(queryWrapper);
            //该用户为进行登录过
            if(userInfo == null){
                //请求用户登录路径
                //第三步 拿着openid  和  access_token请求微信地址，得到扫描人信息
                StringBuilder stringBuilderUserInfo = new StringBuilder();
                StringBuilder appendUserInfo = stringBuilderUserInfo.append("https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s");
                String formatUserInfo = String.format(appendUserInfo.toString(), access_token, openid);
                String userInfoUrl = HttpClientUtils.get(formatUserInfo);
                //新建用户保存信息
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(!StringUtils.isEmpty(userInfo.getName())){
                map.put("name",name);
            }
            if(StringUtils.isEmpty(map.get("name"))){
                map.put("name",userInfo.getNickName());
            }
            if(StringUtils.isEmpty(map.get("name"))){
                map.put("name",userInfo.getPhone());
            }
            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果手机号不为空，返回openid值是空字符串
            //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
            if(StringUtils.isEmpty(userInfo.getPhone())){
                map.put("openid",openid);
            }else{
                map.put("openid","");
            }

            String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());
            map.put("token",token);
            //跳转到前端页面
            return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode((String) map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
