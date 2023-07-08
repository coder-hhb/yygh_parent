package com.hhb.yygh.hosp.controller.admin;

import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.hosp.service.DepartmentService;
import com.hhb.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hospital/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    //查询科室信息
    @GetMapping("/{hoscode}")
    public R getDepartmentList(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list",list);
    }
}
