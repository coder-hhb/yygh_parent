package com.hhb.yygh.order.controller;


import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.order.service.WeiPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/order/wxpay")
public class WxPayController {
    @Autowired
    private WeiPayService weiPayService;
    @GetMapping("/{orderId}")
    public R createNative(@PathVariable Long orderId){
        String url = weiPayService.createNative(orderId);
        return R.ok().data("url",url);
    }
    //获取订单状态
    @GetMapping("getStatus/{orderId}")
    public R getStatus(@PathVariable Long orderId){
        Map<String,String> map = weiPayService.getStatus(orderId);
        if(map == null){
            return R.error().message("查询失败");
        }
        if("SUCCESS".equals(map.get("trade_state"))){
            //更新订单表状态和支付表状态
            weiPayService.updateStatus(orderId,map);
            return R.ok();
        }
        return R.ok().message("支付中");
    }
}
