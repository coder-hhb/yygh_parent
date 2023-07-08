package com.hhb.yygh.task.job;

import com.hhb.yygh.mq.RabbitConst;
import com.hhb.yygh.mq.RabbitService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hhb
 * @data 2023/7/8 13:33
 */
@Component
public class PatientScheduleJob {
    @Autowired
    private RabbitService rabbitService;
    @Scheduled(cron = "*/20 * * * * *")
    public void printTime(){
        System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        rabbitService.sendMessage(RabbitConst.EXCHANGE_DIRECT_TASK,RabbitConst.ROUTING_TASK_8,"");
    }
}
