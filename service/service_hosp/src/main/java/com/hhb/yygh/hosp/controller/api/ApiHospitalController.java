package com.hhb.yygh.hosp.controller.api;

import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.common.config.utils.MD5;
import com.hhb.yygh.hosp.service.HospitalService;
import com.hhb.yygh.hosp.service.HospitalSetService;
import com.hhb.yygh.hosp.utils.HttpRequestHelper;
import com.hhb.yygh.hosp.utils.Result;
import com.hhb.yygh.model.hosp.Hospital;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理")
@RestController
@RequestMapping("/api/hosp/")
public class ApiHospitalController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    //保存医院信息
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request){
        Map<String,Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        String hoscode = (String)resultMap.get("hoscode");
        //获取传过来的密匙
        String signKey = (String)resultMap.get("sign");
        //根据编码查找医院密匙
        String sign = hospitalSetService.getSignByHoscode(hoscode);
        String encrypt = MD5.encrypt(sign);
        if(!StringUtils.isEmpty(signKey) && !StringUtils.isEmpty(sign) && encrypt.equals(signKey)){

            //密匙相同，先转换localDate，再进行添加
            String logoData = (String)resultMap.get("logoData");
            logoData = logoData.replaceAll(" ","+");
            resultMap.put("logoData",logoData);
            hospitalService.saveHospital(resultMap);
            return Result.ok();
        }else{
            throw new YyghException(20001,"添加失败");
        }
    }
    //显示医院信息
    @PostMapping("/hospital/show")
    public Result<Hospital> showHospital(HttpServletRequest request){
        Map<String,Object> resultMap = HttpRequestHelper.switchRequest(request.getParameterMap());
        String hoscode = (String)resultMap.get("hoscode");
        //根据hoscode和sign查找医院
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }
}
