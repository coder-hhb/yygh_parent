package com.hhb.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hhb.yygh.enums.PaymentTypeEnum;
import com.hhb.yygh.enums.RefundStatusEnum;
import com.hhb.yygh.model.order.PaymentInfo;
import com.hhb.yygh.model.order.RefundInfo;
import com.hhb.yygh.order.mapper.RefundInfoMapper;
import com.hhb.yygh.order.service.RefundInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款信息表 服务实现类
 * </p>
 *
 * @author hhb
 * @since 2023-07-07
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo refund(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",paymentInfo.getOrderId());
        //判断是否已经有记录了
        RefundInfo refundInfoExist = baseMapper.selectOne(queryWrapper);
        if(refundInfoExist != null){
            //有记录直接返回
            return refundInfoExist;
        }
        //没有记录需要设置
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject("退款订单");
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
