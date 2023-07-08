package com.hhb.yygh.listener;


import com.hhb.yygh.mq.RabbitConst;
import com.hhb.yygh.mq.RabbitService;
import com.hhb.yygh.service.SmsService;
import com.hhb.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsListener {
    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = RabbitConst.QUEUE_SMS_ITEM),
                    exchange = @Exchange(name = RabbitConst.EXCHANGE_DIRECT_SMS),
                    key = RabbitConst.ROUTING_SMS_ITEM
            )
    })
    public void consume(MsmVo msmVo, Message message, Channel channel){
       smsService.sendMessage(msmVo);
    }
}
