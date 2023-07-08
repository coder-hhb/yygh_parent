package com.hhb.yygh.controller;

import com.hhb.yygh.service.SmsService;
import com.hhb.yygh.common.config.result.R;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Api(tags = "短信发送")
@RestController
@RequestMapping("/user/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/send/{phone}")
    public R snedCode(@PathVariable String phone){
        String code = (String)redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)){
            return R.ok();
        }
        boolean flag = smsService.sendCode(phone);
        return flag ? R.ok() : R.error();
    }
    @GetMapping("/test")
    public R test(){
        String str = "Str";
        return R.ok().data("String",str);
    }

}
