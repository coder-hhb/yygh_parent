package com.hhb.yygh.order.config;

import com.hhb.yygh.mq.RabbitConst;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class OrderConfig {
    //创建交换机
    @Bean
    public Exchange getExchange(){
        return ExchangeBuilder.directExchange(RabbitConst.EXCHANGE_DIRECT_ORDER).durable(true).build();
    }
    //创建队列
    @Bean
    public Queue getQueue(){
        return QueueBuilder.durable(RabbitConst.QUEUE_ORDER).build();
    }
    //绑定交换机和队列
    @Bean
    public Binding binding(
            //import org.springframework.beans.factory.annotation.Qualifier;
            @Qualifier("getQueue") Queue queue,
            @Qualifier("getExchange")Exchange exchange
    ){
            return BindingBuilder.bind(queue).to(exchange).with(RabbitConst.ROUTING_ORDER).noargs();
    }
}
