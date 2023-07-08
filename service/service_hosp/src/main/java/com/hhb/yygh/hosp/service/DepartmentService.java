package com.hhb.yygh.hosp.service;

import com.hhb.yygh.model.hosp.Department;
import com.hhb.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void saveDepartment(Map<String, Object> resultMap);

    Page<Department> getDepartmentPage(Map<String, Object> resultMap);

    void removeDepartment(Map<String, Object> resultMap);

    List<DepartmentVo> getDepartmentList(String hoscode);
}
