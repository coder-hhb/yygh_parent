package com.hhb.yygh.hosp.repository;

import com.hhb.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HospitalRepository extends MongoRepository<Hospital,String> {
    Hospital getHospitalByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);
}
