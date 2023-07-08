package com.hhb.yygh.user.service;

import com.hhb.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-06-12
 */
public interface PatientService extends IService<Patient> {

    List<Patient> getAllPatient(Long userId);

    Patient getParentById(Long id);


}
