package com.hhb.yygh.order.listener;

import com.hhb.yygh.mq.RabbitConst;
import com.hhb.yygh.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.config.xml.AbstractInboundGatewayParser;
import org.springframework.stereotype.Component;

/**
 * @author hhb
 * @data 2023/7/8 13:50
 */
@Component
public class TaskListener {
    @Autowired
    private OrderInfoService orderInfoService;
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value=RabbitConst.QUEUE_TASK_8),
                    exchange = @Exchange(value=RabbitConst.EXCHANGE_DIRECT_TASK),
                    key = RabbitConst.ROUTING_TASK_8
            )
    })
    public void patientRemindJob(Message message, Channel channel){
        orderInfoService.patientRemindJob();
    }
}
