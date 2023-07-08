package com.hhb.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.hhb.yygh")
@MapperScan(value = "com.hhb.yygh.hosp.mapper")
@EnableDiscoveryClient//启动nacos
@EnableFeignClients(basePackages = "com.hhb.yygh")
public class HospApplication {
    public static void main(String[] args){
        SpringApplication.run(HospApplication.class,args);
    }
}
