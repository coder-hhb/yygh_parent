package com.hhb.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.hosp.bean.Actor;
import com.hhb.yygh.hosp.repository.DepartmentRepository;
import com.hhb.yygh.hosp.service.DepartmentService;
import com.hhb.yygh.model.hosp.Department;
import com.hhb.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> resultMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Department.class);
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        //根据hoscode和depcode查询科室
        Department collection = departmentRepository.getByHoscodeAndDepcode(hoscode,depcode);
        if(collection == null){
            department.setIsDeleted(0);
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            departmentRepository.save(department);
        }else{
            department.setId(collection.getId());
            department.setCreateTime(collection.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(collection.getIsDeleted());
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> getDepartmentPage(Map<String, Object> resultMap) {
        int page = Integer.parseInt((String) resultMap.get("page"));
        int limit = Integer.parseInt((String) resultMap.get("limit"));
        String hoscode = (String)resultMap.get("hoscode");
        Department department = new Department();
        department.setHoscode(hoscode);
        Pageable pageable = PageRequest.of(page - 1,limit);
        Example<Department> example = Example.of(department);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void removeDepartment(Map<String, Object> resultMap) {
        String hoscode = (String) resultMap.get("hoscode");
        String dpcode = (String)resultMap.get("depcode");
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode,dpcode);
        if(department!=null){
            departmentRepository.deleteById(department.getId());
        }else{
            throw new YyghException(20001,"当前科室不存在");
        }
    }

    @Override
    public List<DepartmentVo> getDepartmentList(String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        List<Department> all = departmentRepository.findAll(example);
        //根据bigCode进行分组
        //key为当前大科室的bigcode
        //value为当前大科室下的字列表数据
        Map<String, List<Department>> listMap = all.stream().collect(Collectors.groupingBy(Department::getBigcode));
        Set<Map.Entry<String, List<Department>>> entries = listMap.entrySet();
        List<DepartmentVo> departmentVoList = new ArrayList<>();
        for (Map.Entry<String, List<Department>> entry : entries) {
            DepartmentVo departmentVo = new DepartmentVo();
            //大科室编号
            String bigCode = entry.getKey();
            List<Department> departmentList = entry.getValue();
            //大科室名称
            String bigName = departmentList.get(0).getBigname();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(bigName);
            //接下来进行子列表操作
            List<DepartmentVo> childDepartmentVoList = new ArrayList<>();
            for (Department childDepartment : departmentList) {
                DepartmentVo childDepartmentVo = new DepartmentVo();
                childDepartmentVo.setDepcode(childDepartment.getDepcode());
                childDepartmentVo.setDepname(childDepartment.getDepname());
                childDepartmentVoList.add(childDepartmentVo);
            }
            departmentVo.setChildren(childDepartmentVoList);
            departmentVoList.add(departmentVo);
        }
        return departmentVoList;
    }
}
