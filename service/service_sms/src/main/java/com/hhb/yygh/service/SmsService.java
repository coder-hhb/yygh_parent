package com.hhb.yygh.service;

import com.hhb.yygh.vo.msm.MsmVo;

public interface SmsService {
    boolean sendCode(String phone);

    void sendMessage(MsmVo msmVo);
}
