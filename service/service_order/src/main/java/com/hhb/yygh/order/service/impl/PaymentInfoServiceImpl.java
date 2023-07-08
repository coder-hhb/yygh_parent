package com.hhb.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.enums.PaymentStatusEnum;
import com.hhb.yygh.model.order.OrderInfo;
import com.hhb.yygh.model.order.PaymentInfo;
import com.hhb.yygh.order.mapper.PaymentInfoMapper;
import com.hhb.yygh.order.service.PaymentInfoService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        //查找是否已经支付，不能重复支付
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderInfo.getId());
        queryWrapper.eq("payment_type",paymentType);
        Integer count = baseMapper.selectCount(queryWrapper);
        //如果有存在直接return
        if(count > 0){
           return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        //设置状态
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        baseMapper.insert(paymentInfo);
    }
}
