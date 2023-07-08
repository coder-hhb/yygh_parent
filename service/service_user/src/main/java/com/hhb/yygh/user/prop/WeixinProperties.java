package com.hhb.yygh.user.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.apache.naming.SelectorContext.prefix;


@ConfigurationProperties(prefix = "weixin")
@Data
public class WeixinProperties {
    private String appid;
    private String appsecret;
    private String redirecturl;
}
