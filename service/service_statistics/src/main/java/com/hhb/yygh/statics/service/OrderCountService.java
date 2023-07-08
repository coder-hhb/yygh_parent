package com.hhb.yygh.statics.service;

import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderCountVo;

import java.util.Map;

/**
 * @author hhb
 * @data 2023/7/8 14:48
 */
public interface OrderCountService {

    Map<String, Object> countDate(OrderCountQueryVo orderCountQueryVo);
}
