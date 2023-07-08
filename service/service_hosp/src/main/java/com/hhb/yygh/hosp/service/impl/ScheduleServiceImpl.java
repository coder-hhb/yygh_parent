package com.hhb.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.hosp.repository.DepartmentRepository;
import com.hhb.yygh.hosp.repository.HospitalRepository;
import com.hhb.yygh.hosp.repository.ScheduleRepository;
import com.hhb.yygh.hosp.service.ScheduleService;
import com.hhb.yygh.model.hosp.BookingRule;
import com.hhb.yygh.model.hosp.Department;
import com.hhb.yygh.model.hosp.Hospital;
import com.hhb.yygh.model.hosp.Schedule;
import com.hhb.yygh.vo.hosp.BookingScheduleRuleVo;
import com.hhb.yygh.vo.hosp.ScheduleOrderVo;
import com.hhb.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import sun.swing.BakedArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionActivationListener;
import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Override
    public void saveSchedule(Map<String, Object> resultMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule collection = scheduleRepository.getByHoscodeAndDepcodeAndHosScheduleId(hoscode,depcode,hosScheduleId);
        if(collection == null){
            schedule.setIsDeleted(0);
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(0);
            scheduleRepository.save(schedule);
        }else{
            schedule.setIsDeleted(collection.getIsDeleted());
            schedule.setCreateTime(collection.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(collection.getStatus());
            schedule.setId(collection.getId());
            scheduleRepository.save(schedule);
        }

    }

    @Override
    public Page<Schedule> getSchedulePage(Map<String, Object> resultMap) {
        int page = Integer.parseInt((String) resultMap.get("page"));
        int limit = Integer.parseInt((String) resultMap.get("limit"));
        String hoscode = (String)resultMap.get("hoscode");
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        Example<Schedule> example = Example.of(schedule);
        Pageable pageable = PageRequest.of(page - 1,limit);
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void removeSchedule(Map<String, Object> resultMap) {
        String hoscode = (String) resultMap.get("hoscode");
        String hosScheduleId = (String) resultMap.get("hosScheduleId");
        Schedule schedule = scheduleRepository.getByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        scheduleRepository.deleteById(schedule.getId());
    }

    @Override
    public Map<String, Object> getScheduleList(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        //使用mongoTemplate进行聚合
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //查询列表聚合
        //select name,count(*),sum(),sum() from *** where *** group by *** order by *** limit ?,?
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        .count().as("count")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                //分页操作
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)
        );
        //aggregation聚合条件,inputType输入参数,outType输出的参数
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> list = aggregate.getMappedResults();
        //设置日期
        list.stream().forEach(bookingScheduleRuleVo -> {
            String dayOfWeek = this.getDayOfWeek(new DateTime(bookingScheduleRuleVo.getDayOfWeek()));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        });

        //查询总量聚合
        Aggregation aggregationTotal = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregateTotal = mongoTemplate.aggregate(aggregationTotal, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregateTotal.getMappedResults();

        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",mappedResults.size());

        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        Map<String,String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        map.put("baseMap",baseMap);
        return map;
    }

    @Override
    public List<Schedule> getDoctorInTheWorkDate(String hoscode, String depcode, String workDate) {
        Date date = new DateTime(workDate).toDate();
        List<Schedule> list = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);
        list.stream().forEach(schedule -> {
          this.getScheduleDone(schedule);
        });
        return list;
    }
    //完善医生信息
    public void getScheduleDone(Schedule schedule){
        //设置医院名称
        schedule.getParam().put("hosname",hospitalRepository.getHospitalByHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",departmentRepository.getByHoscodeAndDepcode(schedule.getHoscode(),schedule.getDepcode()).getDepname());
        //设置对应日期的星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }
    @Override
    public Map<String, Object> getSchedulePageByCondition(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital == null){
            throw new YyghException(20001,"该医院不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        IPage page = this.getListDate(pageNum, pageSize, bookingRule);
        List<Date> dateList = page.getRecords();
        //接下来进行聚合操作
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        Map<Date, BookingScheduleRuleVo> bookScheduleRuleVoMap = mappedResults.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        List<BookingScheduleRuleVo> list = new ArrayList<>();
        int size = dateList.size();
        for(int i = 0 ; i < size ; i ++){
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = bookScheduleRuleVoMap.get(date);
            if(bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
                bookingScheduleRuleVo.setReservedNumber(0);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(date)));
            bookingScheduleRuleVo.setStatus(0);
            if(i == 0 && pageNum == 1){
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            if(i == size - 1 && pageNum == page.getPages()){
                bookingScheduleRuleVo.setStatus(1);
            }
            list.add(bookingScheduleRuleVo);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("bookingScheduleList",list);
        Map<String,Object> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalRepository.getHospitalByHoscode(hoscode).getHosname());
        //科室
        Department department =departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        map.put("baseMap", baseMap);
        return map;
    }

    @Override
    public List<Schedule> getScheduleListByWorkDate(String hoscode, String depcode, String workDate) {
        Date date = new DateTime(workDate).toDate();
        return scheduleRepository.getByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);

    }

    @Override
    public Schedule getScheduleById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        this.getScheduleDone(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleOrderVoById(String schduleId) {
        Schedule schedule = scheduleRepository.findById(schduleId).get();
        this.getScheduleDone(schedule);
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        //设置医院
        Hospital hospital = hospitalRepository.getHospitalByHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        //设置科室
        scheduleOrderVo.setDepname(departmentRepository.getByHoscodeAndDepcode(schedule.getHoscode(),schedule.getDepcode()).getDepname());
        //设置安排日期
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        //设置安排时间
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        //设置退号截止天数（如：就诊前一天为-1，当天为0）
        BookingRule bookingRule = hospital.getBookingRule();
        Integer quitDay = bookingRule.getQuitDay();
        DateTime dateTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(dateTime.toDate());
        //设置结束时间
        DateTime dateTime1 = this.getDateTime(schedule.getWorkDate(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(dateTime1.toDate());

        return scheduleOrderVo;

    }

    @Override
    public boolean updateAvailableNumber(String scheduleId, Integer availableNumber) {
        //更新剩余预约数量
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
        return true;
    }

    @Override
    public boolean cancel(String scheduleId) {
        Schedule schedule = scheduleRepository.findByHosScheduleId(scheduleId);
        schedule.setAvailableNumber(schedule.getAvailableNumber() + 1);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
        return true;
    }


    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

    //获取排班相关信息
    private IPage getListDate(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
       //获取预约周期
        Integer cycle = bookingRule.getCycle();
        //获取开始预约时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //超出预约时间，周期加1
        if(releaseTime.isBeforeNow()){
            cycle = cycle + 1;
        }
        List<Date> dateList = new ArrayList<>();
        //添加预约周期日期
        for (int i = 0; i < cycle; i++) {
            //计算当前预约日期
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        int size = dateList.size();
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize;
        if(end > size){
            end = size;
        }

        List<Date> pageList = new ArrayList<>();
        for(int i = start ; i < end ; i ++){
            pageList.add(dateList.get(i));
        }

        IPage<Date> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date>(pageNum,pageSize,size);
        page.setRecords(pageList);
        return page;
    }


    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

}
