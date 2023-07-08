package com.hhb.yygh.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhb.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-06-14
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrderInfo(String scheduleId, Long patientId);

    Page<OrderInfo> orderListPage(Integer pageNum, Integer pageSize, OrderQueryVo queryVo);

    OrderInfo getOrderById(Long id);

    void cancelOrder(Long id);

    void patientRemindJob();

    Map<String, Object> statics(OrderCountQueryVo orderCountQueryVo);
}
