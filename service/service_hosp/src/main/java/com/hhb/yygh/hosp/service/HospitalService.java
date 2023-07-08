package com.hhb.yygh.hosp.service;

import com.hhb.yygh.model.hosp.Hospital;
import com.hhb.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    //保存医院信息
    void saveHospital(Map<String, Object> resultMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Hospital getHospitalDetailById(String id);

    List<Hospital> getHospitalByName(String name);

    Hospital getByHoscodeDetail(String hoscode);
}
