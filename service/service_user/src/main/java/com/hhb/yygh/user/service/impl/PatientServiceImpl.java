package com.hhb.yygh.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hhb.yygh.client.DictFeignClient;
import com.hhb.yygh.enums.DictEnum;
import com.hhb.yygh.model.user.Patient;
import com.hhb.yygh.user.mapper.PatientMapper;
import com.hhb.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author hhb
 * @since 2023-06-12
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Autowired
    private DictFeignClient dictFeignClient;
    @Override
    public List<Patient> getAllPatient(Long userId) {
        List<Patient> patientList = baseMapper.selectList(new QueryWrapper<Patient>().eq("user_id", userId));
        //设置用户的地区信息
        patientList.stream().forEach(patient -> {
            this.setPatientInfomation(patient);
        });
        return patientList;
    }

    @Override
    public Patient getParentById(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.setPatientInfomation(patient);
        return patient;
    }


    //完成就诊人信息
    public void setPatientInfomation(Patient patient){
        //证件号码
        String certificatesTypeString = dictFeignClient.getSelectedNameByValue(DictEnum.CERTIFICATES_TYPE.getDictCode(), Long.parseLong(patient.getCertificatesType()));
        //联系人证件号码
        String contactCertificatesTypeString = dictFeignClient.getSelectedNameByValue(DictEnum.CERTIFICATES_TYPE.getDictCode(), Long.parseLong(patient.getContactsCertificatesType()));
        String provinceString = dictFeignClient.getNameByValue(Long.parseLong(patient.getProvinceCode()));
        String cityString= dictFeignClient.getNameByValue(Long.parseLong(patient.getCityCode()));
        String districtString = dictFeignClient.getNameByValue(Long.parseLong(patient.getDistrictCode()));
        patient.getParam().put("provinceString",provinceString);
        patient.getParam().put("cityString",cityString);
        patient.getParam().put("districtString",districtString);
        patient.getParam().put("fullAddress",provinceString + cityString + districtString + patient.getAddress());
        patient.getParam().put("certificatesTypeString",certificatesTypeString);
        patient.getParam().put("contactCertificatesTypeString",contactCertificatesTypeString);
    }
}
