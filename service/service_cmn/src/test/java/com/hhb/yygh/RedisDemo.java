package com.hhb.yygh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest(classes = CmnApplication.class)
@RunWith(SpringRunner.class)
public class RedisDemo {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testInsert(){
        redisTemplate.opsForValue().set("test","测试插入数据");
    }
}

