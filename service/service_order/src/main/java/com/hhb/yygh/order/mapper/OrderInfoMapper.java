package com.hhb.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhb.yygh.model.order.OrderInfo;
import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author hhb
 * @since 2023-06-14
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> statics(OrderCountQueryVo orderCountQueryVo);
}
