package com.hhb.yygh.hosp.controller.user;

import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.hosp.service.DepartmentService;
import com.hhb.yygh.hosp.service.HospitalService;
import com.hhb.yygh.hosp.service.ScheduleService;
import com.hhb.yygh.model.hosp.Hospital;
import com.hhb.yygh.vo.hosp.DepartmentVo;
import com.hhb.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;
    @GetMapping("/list")
    public R getHospitalList(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> page= hospitalService.getHospitalPage(1, 10000, hospitalQueryVo);
        return R.ok().data("list",page.getContent());
    }

    @GetMapping("/{name}")
    public R getHospitalByName(@PathVariable String name){
       List<Hospital> list = hospitalService.getHospitalByName(name);
       return R.ok().data("list",list);
    }

    @GetMapping("detail/{hoscode}")
    public R getHospitaoByHoscode(@PathVariable String hoscode){
        Hospital hospital = hospitalService.getByHoscodeDetail(hoscode);
        return R.ok().data("hospital",hospital);
    }

    @GetMapping("getDepartment/{hoscode}")
    public R getDepartmentByHoscode(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list",list);
    }

}
