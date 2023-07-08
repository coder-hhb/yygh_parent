package com.hhb.yygh.order.client;

import com.hhb.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author hhb
 * @data 2023/7/8 17:15
 */
@FeignClient("service-orders")
public interface OrderStaticsClient {
    //统计预约数量
    @PostMapping("/user/order/orderinfo/statics")
    public Map<String,Object> statics(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
