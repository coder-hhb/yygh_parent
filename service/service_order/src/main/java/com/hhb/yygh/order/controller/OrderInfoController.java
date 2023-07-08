package com.hhb.yygh.order.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.enums.OrderStatusEnum;
import com.hhb.yygh.model.order.OrderInfo;
import com.hhb.yygh.order.service.OrderInfoService;
import com.hhb.yygh.order.utils.JwtHelper;
import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author hhb
 * @since 2023-06-14
 */
@RestController
@RequestMapping("/user/order/orderinfo")
public class OrderInfoController {
    @Autowired
    private OrderInfoService orderInfoService;
    @PostMapping("/save/{scheduleId}/{patientId}")
    public R saveOrderInfo(
            @PathVariable String scheduleId,
            @PathVariable Long patientId
    ){
        Long orderId = orderInfoService.saveOrderInfo(scheduleId,patientId);
        return R.ok().data("orderId",orderId);
    }
    //获取订单列表
    @GetMapping("/{pageNum}/{pageSize}")
    public R orderList(@PathVariable Integer pageNum,
                       @PathVariable Integer pageSize,
                       OrderQueryVo orderQueryVo,
                       @RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> page = orderInfoService.orderListPage(pageNum,pageSize,orderQueryVo);
        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());
    }
    //获取订单状态列表
    @GetMapping("/statusList")
    public R statusList(){
        return R.ok().data("statusList", OrderStatusEnum.getStatusList());
    }
    //取消订单
    @PostMapping("/cancelorder/{id}")
    public R cancelOrder(@PathVariable Long id){
        orderInfoService.cancelOrder(id);
        return R.ok();
    }
    //根据订单id查询
    @GetMapping("/getOrder/{id}")
    public R getOrder(@PathVariable Long id){
        OrderInfo orderInfo = orderInfoService.getOrderById(id);
        return R.ok().data("orderInfo",orderInfo);
    }
    //统计预约数量
    @PostMapping("/statics")
    public Map<String,Object> statics(@RequestBody(required = false) OrderCountQueryVo orderCountQueryVo){
        return orderInfoService.statics(orderCountQueryVo);
    }
}

