package com.hhb.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhb.yygh.enums.PaymentStatusEnum;
import com.hhb.yygh.model.order.PaymentInfo;
import com.hhb.yygh.mq.RabbitConfig;
import com.hhb.yygh.mq.RabbitConst;
import com.hhb.yygh.mq.RabbitService;
import com.hhb.yygh.order.service.PaymentInfoService;
import com.hhb.yygh.order.service.WeiPayService;
import com.hhb.yygh.user.client.PatientFeignClient;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.enums.OrderStatusEnum;
import com.hhb.yygh.hosp.client.ScheduleFeignClient;
import com.hhb.yygh.model.order.OrderInfo;
import com.hhb.yygh.model.user.Patient;
import com.hhb.yygh.order.mapper.OrderInfoMapper;
import com.hhb.yygh.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhb.yygh.order.utils.HttpRequestHelper;
import com.hhb.yygh.vo.hosp.ScheduleOrderVo;
import com.hhb.yygh.vo.msm.MsmVo;
import com.hhb.yygh.vo.order.OrderCountQueryVo;
import com.hhb.yygh.vo.order.OrderCountVo;
import com.hhb.yygh.vo.order.OrderMqVo;
import com.hhb.yygh.vo.order.OrderQueryVo;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author hhb
 * @since 2023-06-14
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private ScheduleFeignClient scheduleFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private WeiPayService weiPayService;
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public Long saveOrderInfo(String scheduleId, Long patientId) {

        //获取医生排班信息
        ScheduleOrderVo scheduleOrderVo = scheduleFeignClient.getScheduleByScheduleId(scheduleId);
        //判断当前时间是否已过了预约时间
        if(new DateTime(scheduleOrderVo.getStopTime()).isBeforeNow()){
            //如果已经超过了，就说明无法预约了
            throw new YyghException(20001,"当前时间已超过挂号时间");
        }
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientById(patientId);
        Map<String,Object> paramMap = new HashMap<>();
        //向第三方医院发出请求
        paramMap.put("hoscode",scheduleOrderVo.getHoscode());
        paramMap.put("depcode",scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate",scheduleOrderVo.getReserveDate());
        paramMap.put("reserveTime",scheduleOrderVo.getReserveTime());
        paramMap.put("amount",scheduleOrderVo.getAmount());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        if(jsonObject != null && jsonObject.getInteger("code") == 200){
            JSONObject data = jsonObject.getJSONObject("data");

            OrderInfo orderInfo = new OrderInfo();
            //设置用户id
            orderInfo.setUserId(patient.getUserId());
            //设置订单交易号
            String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(1000);
            orderInfo.setOutTradeNo(outTradeNo);
            //设置医院编号
            orderInfo.setHoscode(scheduleOrderVo.getHoscode());
            //设置医院名成
            orderInfo.setHosname(scheduleOrderVo.getHosname());
            //设置科室编号
            orderInfo.setDepcode(scheduleOrderVo.getDepcode());
            //设置科室名称
            orderInfo.setDepname(scheduleOrderVo.getDepname());
            //设置标题
            orderInfo.setTitle(scheduleOrderVo.getTitle());
            //设置排班编号
            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
            //设置安排日期
            orderInfo.setReserveDate(scheduleOrderVo.getReserveDate());
            //设置安排时间
            orderInfo.setReserveTime(scheduleOrderVo.getReserveTime());
            //设置就诊人id
            orderInfo.setPatientId(patient.getId());
            //设置就诊人名称
            orderInfo.setPatientName(patient.getName());
            //设置就诊人电话
            orderInfo.setPatientPhone(patient.getPhone());
            //设置预约记录唯一标识
            orderInfo.setHosRecordId(data.getString("hosRecordId"));
            //设置预约号序
            orderInfo.setNumber(data.getInteger("number"));
            //设置建议取号时间
            orderInfo.setFetchTime(data.getString("fetchTime"));
            //设置取号地址
            orderInfo.setFetchAddress(data.getString("fetchAddress"));
            //设置服务费
            orderInfo.setAmount(scheduleOrderVo.getAmount());
            //设置退号时间
            orderInfo.setQuitTime(scheduleOrderVo.getQuitTime());
            //设置支付状态
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
            //保存到数据库
            baseMapper.insert(orderInfo);
            //整合orderMqVo发送短信信息
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setAvailableNumber(data.getInteger("availableNumber"));
            orderMqVo.setReservedNumber(data.getInteger("reservedNumber"));
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            msmVo.setTemplateCode("您已经预约上午${time}${name}的医生的号");
            Map<String,Object> msmMap = new HashMap<>();
            msmMap.put("time",scheduleOrderVo.getReserveDate() + ":" + scheduleOrderVo.getReserveTime());
            msmMap.put("name","xxx");
            msmVo.setParam(msmMap);
            orderMqVo.setMsmVo(msmVo);
            //把msmvo传到交换机
            rabbitService.sendMessage(RabbitConst.EXCHANGE_DIRECT_ORDER,RabbitConst.ROUTING_ORDER,orderMqVo);
            return orderInfo.getId();
        }else{
            throw new YyghException(20001,"预约人数已满，无法预约");
        }
    }

    @Override
    public Page<OrderInfo> orderListPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo) {
        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        //orderQueryVo获取条件值
        Long userId = orderQueryVo.getUserId();
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //对条件值进行非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userId)) {
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname",name);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date",reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        orderInfoService.page(page,wrapper);
        page.getRecords().stream().forEach(orderInfo -> {
            this.orderInfoDone(orderInfo);
        });
        return page;
    }

    @Override
    public OrderInfo getOrderById(Long id) {
        OrderInfo orderInfo = baseMapper.selectById(id);
        this.orderInfoDone(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long id) {
        //确定当前取消时间和挂号取消截止时间，进行对比
        OrderInfo orderInfo = baseMapper.selectById(id);
        Date quitTime = orderInfo.getQuitTime();
        DateTime dateTime = new DateTime(quitTime);
        if(dateTime.isBeforeNow()){
            //已经超过取消预约截止时间
            throw new YyghException(20001,"当过挂号截止时间");
        }
        //没有超过，通知第三方医院，进行取消操作
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("hosRecordId",orderInfo.getHosRecordId());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/updateCancelStatus");
        //查看是否转发成功
        if(jsonObject.getIntValue("code") != 200 || jsonObject==null){
            //转发失败
            throw new YyghException(20001,"取消失败");
        }
        //查找成功
        //判断用户是否已支付
        if(orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus()){
            //该用户已支付
            //进行退款操作
            boolean flag = weiPayService.refund(orderInfo.getId());
            if(!flag){
                //退款失败
                throw new YyghException(20001,"退款失败");
            }
        }
        //更新订单状态以及支付表的状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderInfo.getId());
        PaymentInfo paymentInfo = paymentInfoService.getOne(queryWrapper);
        if(paymentInfo != null){
            paymentInfo.setPaymentStatus(PaymentStatusEnum.REFUND.getStatus());
            paymentInfo.setUpdateTime(new Date());
            paymentInfoService.updateById(paymentInfo);
        }
        //更新医生的剩余可预约数信息
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        //给就诊人发送提示信息
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone("13414020483");
        msmVo.setTemplateCode("xxxx....");
        orderMqVo.setMsmVo(msmVo);
        rabbitService.sendMessage(RabbitConst.EXCHANGE_DIRECT_ORDER,RabbitConst.ROUTING_ORDER,orderMqVo);

    }

    @Override
    public void patientRemindJob() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        //查询今天所有的预约数
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.ne("order_status",-1);
        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);
        orderInfoList.forEach(orderInfo -> {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode(orderInfo.getPatientName());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(RabbitConst.EXCHANGE_DIRECT_SMS,RabbitConst.ROUTING_SMS_ITEM,msmVo);
        });
    }

    @Override
    public Map<String, Object> statics(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> list = baseMapper.statics(orderCountQueryVo);
        List<Integer> countList = list.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        List<String> dateList = list.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }


    //完善订单信息
    public void orderInfoDone(OrderInfo orderInfo){
        orderInfo.getParam().put("orderStatusString",OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
    }
}
