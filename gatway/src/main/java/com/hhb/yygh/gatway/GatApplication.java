package com.hhb.yygh.gatway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class GatApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatApplication.class,args);
    }
}
