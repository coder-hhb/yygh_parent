package com.hhb.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhb.yygh.model.order.PaymentInfo;
import com.hhb.yygh.model.order.RefundInfo;

/**
 * <p>
 * 退款信息表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-07-07
 */
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo refund(PaymentInfo paymentInfo);
}
