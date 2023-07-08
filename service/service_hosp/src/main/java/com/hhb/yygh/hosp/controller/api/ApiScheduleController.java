package com.hhb.yygh.hosp.controller.api;

import com.hhb.yygh.hosp.service.ScheduleService;
import com.hhb.yygh.hosp.utils.HttpRequestHelper;
import com.hhb.yygh.hosp.utils.Result;
import com.hhb.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "上传排版")
@RestController
@RequestMapping("/api/hosp/")
public class ApiScheduleController {
    @Autowired
    private ScheduleService scheduleService;


    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        scheduleService.saveSchedule(resultMap);
        return Result.ok();
    }
    @PostMapping("/schedule/list")
    public Result scheduleList(HttpServletRequest request){
        Map<String, Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        Page<Schedule> page = scheduleService.getSchedulePage(resultMap);
        return Result.ok(page);
    }
    @PostMapping("/schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        scheduleService.removeSchedule(resultMap);
        return Result.ok();
    }
}
