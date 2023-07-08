package com.hhb.yygh.hosp.listener;


import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.hosp.service.ScheduleService;
import com.hhb.yygh.mq.RabbitConst;
import com.hhb.yygh.mq.RabbitService;
import com.hhb.yygh.vo.msm.MsmVo;
import com.hhb.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HospListener {
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private ScheduleService scheduleService;
    @RabbitListener(bindings = {
            @QueueBinding(
                    //队列
                    value = @Queue(name = RabbitConst.QUEUE_ORDER),
                    //交换机
                    exchange = @Exchange(name = RabbitConst.EXCHANGE_DIRECT_ORDER),
                    //路由键
                    key = RabbitConst.ROUTING_ORDER
            )
    })
    //确认挂号
    public void consume(OrderMqVo orderMqVo, Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        boolean flag = false;
        //剩余可预约数不为空，证明是确认挂号
        if(availableNumber != null){
            flag = scheduleService.updateAvailableNumber(scheduleId,availableNumber);
            MsmVo msmVo = orderMqVo.getMsmVo();
            if(flag && msmVo != null){
                //发送短信
                rabbitService.sendMessage(RabbitConst.EXCHANGE_DIRECT_SMS,RabbitConst.ROUTING_SMS_ITEM,msmVo);
            }else{
                throw new YyghException(20001,"发送失败");
            }
        }else{
            //剩余可预约数不为空，证明是取消预约
            flag = scheduleService.cancel(scheduleId);
            MsmVo msmVo = orderMqVo.getMsmVo();
            if(flag && msmVo != null){
                //发送短信
                rabbitService.sendMessage(RabbitConst.EXCHANGE_DIRECT_SMS,RabbitConst.ROUTING_SMS_ITEM,msmVo);
            }else{
                throw new YyghException(20001,"发送失败");
            }
        }
    }

}
