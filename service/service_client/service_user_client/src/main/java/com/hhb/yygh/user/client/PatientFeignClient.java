package com.hhb.yygh.user.client;

import com.hhb.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-user")
public interface PatientFeignClient {
     @GetMapping("/user/userinfo/patient/getPatientById/{patientId}")
    public Patient getPatientById(@PathVariable("patientId") Long patientId);
}
