package com.hhb.yygh.order;


import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//注意这个basePackages的值要和被调用client的启动类保持一致
@EnableFeignClients(basePackages = {"com.hhb.yygh"})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.hhb"})
@MapperScan(basePackages = {"com.hhb.yygh.order.mapper"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
