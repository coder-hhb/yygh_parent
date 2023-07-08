package com.hhb.yygh.hosp.controller.api;

import com.hhb.yygh.hosp.service.DepartmentService;
import com.hhb.yygh.hosp.utils.HttpRequestHelper;
import com.hhb.yygh.hosp.utils.Result;
import com.hhb.yygh.model.hosp.Department;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "上传科室")
@RestController
@RequestMapping("/api/hosp/")
public class ApiDepartmentController {
    @Autowired
    private DepartmentService departmentService;
    //添加科室功能
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        departmentService.saveDepartment(resultMap);
        return Result.ok();
    }

    //展示科室功能
    @PostMapping("/department/list")
    public Result<Page> showList(HttpServletRequest request){
        Map<String, Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        Page<Department> page = departmentService.getDepartmentPage(resultMap);
        return Result.ok(page);
    }

    //删除科室功能
    @PostMapping("/department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        departmentService.removeDepartment(resultMap);
        return Result.ok();
    }

}
