package com.hhb.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hhb.yygh.client.DictFeignClient;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.enums.DictEnum;
import com.hhb.yygh.hosp.repository.HospitalRepository;
import com.hhb.yygh.hosp.service.HospitalService;
import com.hhb.yygh.model.hosp.Hospital;
import com.hhb.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DictFeignClient dictFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveHospital(Map<String, Object> resultMap) {
        //根据hoscode查找是否有相应的医院，没有的话直接添加，有的话进行修改
        //先把resultMap转化为hospital
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Hospital.class);
        //根据hoscode查找
        Hospital collection = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        if(collection == null){
            //当前医院不存在
            //设置状态0未上线1已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            //保存
            hospitalRepository.save(hospital);
        }else{
            //当前医院存在
            //进行修改
            hospital.setStatus(collection.getStatus());
            hospital.setCreateTime(collection.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(collection.getIsDeleted());
            hospital.setId(collection.getId());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital != null){
            return hospital;
        }else{
            throw new YyghException(20001,"医院不存在");
        }
    }

    @Override
    public Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.contains()) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Example<Hospital> example = Example.of(hospital,matcher);
        Pageable pageable = PageRequest.of(pageNum - 1,pageSize, Sort.by("createTime").descending());
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);
        //完成分页查询后，还需要完善医院类型，医院的省市功能
        all.getContent().stream().forEach(item -> {
            this.setHospitalDone(item);
        });
        return all;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if(status == 0 || status == 1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospitalDetailById(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        this.setHospitalDone(hospital);
        return hospital;
    }

    @Override
    public List<Hospital> getHospitalByName(String name) {
        return hospitalRepository.findByHosnameLike(name);
    }

    @Override
    public Hospital getByHoscodeDetail(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        this.setHospitalDone(hospital);
        return hospital;
    }

    public void setHospitalDone(Hospital hospital){
        //医院等级
        String hostypeString = dictFeignClient.getSelectedNameByValue(DictEnum.HOSTYPE.getDictCode(),Long.parseLong(hospital.getHostype()));
        String provinceAddress = dictFeignClient.getNameByValue(Long.parseLong(hospital.getProvinceCode()));
        String cityAddress = dictFeignClient.getNameByValue(Long.parseLong(hospital.getCityCode()));
        String DistrictAddress = dictFeignClient.getNameByValue(Long.parseLong(hospital.getDistrictCode()));
        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAddress",provinceAddress + cityAddress + DistrictAddress + hospital.getAddress());
    }
}
