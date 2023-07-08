package com.hhb.yygh.hosp.repository;

import com.hhb.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule getByHoscodeAndDepcodeAndHosScheduleId(String hoscode, String depcode,String hosScheduleId);

    Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);

    List<Schedule> getByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);

    Schedule findByHosScheduleId(String scheduleId);
}
