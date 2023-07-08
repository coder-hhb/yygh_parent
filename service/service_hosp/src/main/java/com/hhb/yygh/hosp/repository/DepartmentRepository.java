package com.hhb.yygh.hosp.repository;

import com.hhb.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department getByHoscodeAndDepcode(String hoscode, String depcode);
}
