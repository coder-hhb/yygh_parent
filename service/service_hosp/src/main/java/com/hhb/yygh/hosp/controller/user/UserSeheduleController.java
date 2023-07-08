package com.hhb.yygh.hosp.controller.user;


import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.hosp.service.ScheduleService;
import com.hhb.yygh.model.hosp.Schedule;
import com.hhb.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp/schedule")
public class UserSeheduleController {
    @Autowired
    private ScheduleService scheduleService;
    //获取排班信息
    @GetMapping("/getSchedule/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getSchedulePageByCondition(@PathVariable Integer pageNum,
                                        @PathVariable Integer pageSize,
                                        @PathVariable String hoscode,
                                        @PathVariable String depcode){
        Map<String,Object> map = scheduleService.getSchedulePageByCondition(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }

    //获取某一天值班医生信息
    @GetMapping("/getScheduleList/{hoscode}/{depcode}/{workDate}")
    public R getScheduleList(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate
    ){
       List<Schedule> scheduleList =  scheduleService.getDoctorInTheWorkDate(hoscode,depcode,workDate);
       return R.ok().data("scheduleList",scheduleList);
    }
    //根据id获取排班基本信息
    @GetMapping("/getScheduleById/{id}")
    public R getScheduleById(@PathVariable String id){
        Schedule schedule = scheduleService.getScheduleById(id);
        return R.ok().data("schedule",schedule);
    }

    @GetMapping("/getScheduleByScheduleId/{scheduleId}")
    public ScheduleOrderVo getScheduleByScheduleId(@PathVariable("scheduleId") String scheduleId){
        return scheduleService.getScheduleOrderVoById(scheduleId);
    }
}
