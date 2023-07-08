package com.hhb.yygh.hosp.service;

import com.hhb.yygh.model.hosp.Schedule;
import com.hhb.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> resultMap);

    Page<Schedule> getSchedulePage(Map<String, Object> resultMap);

    void removeSchedule(Map<String, Object> resultMap);

    Map<String, Object> getScheduleList(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> getDoctorInTheWorkDate(String hoscode, String depcode, String workDate);

    Map<String, Object> getSchedulePageByCondition(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> getScheduleListByWorkDate(String hoscode, String depcode, String workDate);

    Schedule getScheduleById(String id);

    ScheduleOrderVo getScheduleOrderVoById(String schduleId);

    boolean updateAvailableNumber(String scheduleId, Integer availableNumber);

    boolean cancel(String scheduleId);
}
