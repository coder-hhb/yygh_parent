package com.hhb.yygh.hosp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hhb.yygh.model.hosp.HospitalSet;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-06-03
 */
public interface HospitalSetService extends IService<HospitalSet> {

    String getSignByHoscode(String hoscode);
}
