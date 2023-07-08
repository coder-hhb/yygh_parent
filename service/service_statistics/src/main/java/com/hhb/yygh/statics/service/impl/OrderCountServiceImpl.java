package com.hhb.yygh.statics.service.impl;

import com.hhb.yygh.order.client.OrderStaticsClient;
import com.hhb.yygh.statics.service.OrderCountService;
import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author hhb
 * @data 2023/7/8 14:48
 */
@Service
public class OrderCountServiceImpl implements OrderCountService {
    @Autowired
    private OrderStaticsClient orderStaticsClient;
    @Override
    public Map<String, Object> countDate(OrderCountQueryVo orderCountQueryVo) {
       return orderStaticsClient.statics(orderCountQueryVo);
    }
}
