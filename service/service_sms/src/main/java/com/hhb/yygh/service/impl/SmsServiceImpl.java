package com.hhb.yygh.service.impl;

import com.hhb.yygh.service.SmsService;
import com.hhb.yygh.utils.HttpUtils;
import com.hhb.yygh.utils.RandomUtil;
import com.hhb.yygh.vo.msm.MsmVo;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean sendCode(String phone) {

        String host = "https://dfsmsv2.market.alicloudapi.com";
        String path = "/data/send_sms_v2";
        String method = "POST";
        String appcode = "d752f48b797f495ea2ea46c8f7c6b279";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        String fourBitRandom = RandomUtil.getFourBitRandom();
        System.out.println(fourBitRandom);
        bodys.put("content", "code:"+ fourBitRandom);
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
            //将验证码保存到redis
            redisTemplate.opsForValue().set(phone,fourBitRandom,15, TimeUnit.DAYS);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendMessage(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        String templateCode = msmVo.getTemplateCode();
        System.out.println("给就诊人的手机：" + phone + "发送短信成功" + templateCode);
    }

}
