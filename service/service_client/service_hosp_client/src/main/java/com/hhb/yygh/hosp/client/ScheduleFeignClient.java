package com.hhb.yygh.hosp.client;

import com.hhb.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp")
public interface ScheduleFeignClient {
    @GetMapping("/user/hosp/schedule/getScheduleByScheduleId/{scheduleId}")
    public ScheduleOrderVo getScheduleByScheduleId(@PathVariable("scheduleId") String scheduleId);
}
