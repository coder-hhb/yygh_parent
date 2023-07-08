package com.hhb.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.enums.OrderStatusEnum;
import com.hhb.yygh.enums.PaymentStatusEnum;
import com.hhb.yygh.enums.PaymentTypeEnum;
import com.hhb.yygh.enums.RefundStatusEnum;
import com.hhb.yygh.model.order.OrderInfo;
import com.hhb.yygh.model.order.PaymentInfo;
import com.hhb.yygh.model.order.RefundInfo;
import com.hhb.yygh.order.prop.WeiPayProperties;
import com.hhb.yygh.order.service.OrderInfoService;
import com.hhb.yygh.order.service.PaymentInfoService;
import com.hhb.yygh.order.service.RefundInfoService;
import com.hhb.yygh.order.service.WeiPayService;
import com.hhb.yygh.order.utils.HttpClient;
import io.netty.channel.socket.ChannelOutputShutdownException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiPayServiceImpl implements WeiPayService {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private WeiPayProperties weiPayProperties;
    @Autowired
    private RefundInfoService refundInfoService;
    @Override
    public String createNative(Long orderId) {
        try {
            OrderInfo orderInfo = orderInfoService.getOrderById(orderId);
            //保存订单信息
            paymentInfoService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());

            Map<String,String> paramMap = new HashMap<>();
            //设置
            paramMap.put("appid", weiPayProperties.getAppid());
            paramMap.put("mch_id", weiPayProperties.getPartner());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            Date reserveDate = orderInfo.getReserveDate();
            String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
            String body = reserveDateString + "就诊"+ orderInfo.getDepname();
            paramMap.put("body",body);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            //测试数据
            paramMap.put("total_fee","1");
            paramMap.put("spbill_create_ip","127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");
            //HTTPClient来根据URL访问第三方接口并且传递参数
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String xml = WXPayUtil.generateSignedXml(paramMap, weiPayProperties.getPartnerkey());
            //设置xml
            httpClient.setXmlParam(xml);
            //支持https协议
            httpClient.setHttps(true);
            httpClient.post();
            //返回第三方的数据
            String xmlResult = httpClient.getContent();
            //转化为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            System.out.println(resultMap);
            return resultMap.get("code_url");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public Map<String, String> getStatus(Long orderId) {
        try {
            OrderInfo order = orderInfoService.getOrderById(orderId);
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",weiPayProperties.getAppid());
            paramMap.put("mch_id",weiPayProperties.getPartner());
            paramMap.put("out_trade_no",order.getOutTradeNo());
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap,weiPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.post();

            String xml = httpClient.getContent();
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(xml);
            return stringStringMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateStatus(Long orderId, Map<String, String> map) {
        //更新订单表状态
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
        //更新支付表状态
        UpdateWrapper<PaymentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id",orderId);
        updateWrapper.set("trade_no",map.get("transaction_id"));
        updateWrapper.set("callback_time",new Date());
        updateWrapper.set("callback_content", JSONObject.toJSONString(map));
        updateWrapper.set("payment_status",PaymentStatusEnum.PAID.getStatus());
        paymentInfoService.update(updateWrapper);
    }

    @Override
    public boolean refund(Long id) {
        try {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",id);
        PaymentInfo paymentInfo = paymentInfoService.getOne(queryWrapper);
        RefundInfo refundInfo = refundInfoService.refund(paymentInfo);
        //如果已退款
        if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()){
            //直接返回true
            return true;
        }

            //执行微信退款
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",weiPayProperties.getAppid());       //公众账号ID
            paramMap.put("mch_id",weiPayProperties.getPartner());   //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信支付订单号
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
            paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
            //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee","1");
            paramMap.put("refund_fee","1");
            String paramXml = WXPayUtil.generateSignedXml(paramMap,weiPayProperties.getPartnerkey());
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            httpClient.setHttps(true);
            httpClient.setCert(true);
            //设置证书密码
            httpClient.setCertPassword(weiPayProperties.getPartner());
            httpClient.setXmlParam(paramXml);
            //发送httpclient
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
           //退款成功
            if("SUCCESS".equals(map.get("result_code"))){
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setTradeNo(map.get("refund_id"));
                refundInfo.setCallbackTime(new Date());
                refundInfo.setCallbackContent(JSONObject.toJSONString(map));
                refundInfoService.updateById(refundInfo);
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
