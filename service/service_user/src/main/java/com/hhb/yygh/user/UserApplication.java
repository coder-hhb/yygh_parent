package com.hhb.yygh.user;

import com.hhb.yygh.user.prop.WeixinProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hhb")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hhb")
@MapperScan("com.hhb.yygh.user.mapper")
@EnableConfigurationProperties(value = WeixinProperties.class)
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
