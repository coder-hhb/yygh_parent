package com.hhb.yygh.order.service;

import java.util.Map;

public interface WeiPayService {
    String createNative(Long orderId);

    Map<String, String> getStatus(Long orderId);

    void updateStatus(Long orderId, Map<String, String> map);

    boolean refund(Long id);
}
