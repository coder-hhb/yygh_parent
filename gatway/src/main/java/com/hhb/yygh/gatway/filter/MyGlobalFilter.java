package com.hhb.yygh.gatway.filter;

import com.google.common.net.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

//@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //登录接口不进行拦截
        if(antPathMatcher.match("/admin/user/**",path)){
            //直接放行
            return chain.filter(exchange);
        }else{
            //进行拦截操作，必须登录才能通过
            List<String> strings = request.getHeaders().get("X-Token");
            if(strings == null){
                //没有相关信息,跳转到登录界面
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://localhost:9528");
                //结束请求
                return response.setComplete();
            }else{
                return chain.filter(exchange);
            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
