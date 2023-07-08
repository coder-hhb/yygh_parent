package com.hhb.yygh.hosp.controller.admin;


import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.hosp.service.ScheduleService;
import com.hhb.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hospital/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    //查找医院排班信息
    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getScheduleList(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize,
                             @PathVariable String hoscode,
                             @PathVariable String depcode){
        Map<String,Object> map = scheduleService.getScheduleList(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }

    //查找同一天下的医生信息
    @GetMapping("/{hoscode}/{depcode}/{workDate}")
    public R getDoctorInTheWorkDate(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list = scheduleService.getDoctorInTheWorkDate(hoscode,depcode,workDate);
        return R.ok().data("list",list);
    }


}
