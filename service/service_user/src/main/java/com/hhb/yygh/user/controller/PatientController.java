package com.hhb.yygh.user.controller;


import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.model.user.Patient;
import com.hhb.yygh.user.service.PatientService;
import com.hhb.yygh.user.utils.JwtHelper;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author hhb
 * @since 2023-06-12
 */
@Api(tags = "就诊人模块")
@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;
    @GetMapping("/getAllPatient")
    public R getAllPatient(@RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        List<Patient> list = patientService.getAllPatient(userId);
        return R.ok().data("list",list);
    }

    @PostMapping("/save")
    public R savePatient(@RequestHeader String token,@RequestBody Patient patient){
        Long userId = JwtHelper.getUserId(token);
        //设置就诊者的userid
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    @PutMapping("/update")
    public R updatePathent(@RequestBody Patient patient){
        patientService.updateById(patient);
        return R.ok();
    }

    @GetMapping("getPatient/{id}")
    public R getPathentById(@PathVariable Long id){
        Patient patient = patientService.getParentById(id);
        return R.ok().data("patient",patient);
    }

    @DeleteMapping("delete/{id}")
    public R delete(@PathVariable Long id){
        patientService.removeById(id);
        return R.ok();
    }

    //获取就诊人信息
    @GetMapping("/getPatientById/{patientId}")
    public Patient getPatientById(@PathVariable("patientId") Long patientId){
        return patientService.getById(patientId);
    }


}

