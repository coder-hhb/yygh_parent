package com.hhb.yygh.order.service;

import com.hhb.yygh.model.order.OrderInfo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhb.yygh.model.order.PaymentInfo;

/**
 * <p>
 * 支付信息表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-06-15
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    //paymentType 支付类型（1：微信 2：支付宝）
    void savePaymentInfo(OrderInfo orderInfo,Integer paymentType);
}
