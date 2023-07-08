package com.hhb.yygh.statics.controller;

import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.statics.service.OrderCountService;
import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hhb
 * @data 2023/7/8 14:32
 */
@RestController
@RequestMapping("/admin/static")
public class OrderStaticsController {
    @Autowired
    private OrderCountService orderCountService;
    @GetMapping("countByData")
    public R countByData(OrderCountQueryVo orderCountQueryVo){
        Map<String,Object> map =  orderCountService.countDate(orderCountQueryVo);
        return R.ok().data(map);
    }
}
