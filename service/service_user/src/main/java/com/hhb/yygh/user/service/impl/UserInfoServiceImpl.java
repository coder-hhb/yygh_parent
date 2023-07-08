package com.hhb.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhb.yygh.common.config.exception.YyghException;
import com.hhb.yygh.enums.AuthStatusEnum;
import com.hhb.yygh.model.acl.User;
import com.hhb.yygh.model.user.Patient;
import com.hhb.yygh.model.user.UserInfo;
import com.hhb.yygh.user.mapper.UserInfoMapper;
import com.hhb.yygh.user.service.PatientService;
import com.hhb.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhb.yygh.user.utils.JwtHelper;
import com.hhb.yygh.vo.user.LoginVo;
import com.hhb.yygh.vo.user.UserAuthVo;
import com.hhb.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hhb
 * @since 2023-06-10
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PatientService patientService;
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(20001,"手机号和验证码不能为空");
        }
        //这里phone不能加双引号！！！
        String redisCode = (String)redisTemplate.opsForValue().get(phone);
        if(StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            throw new YyghException(20001,"验证码不正确");
        }
        Map<String, Object> map = new HashMap<>();
        //获取openid
        String openid = loginVo.getOpenid();
        if(StringUtils.isEmpty(openid)){
            //用户第一次登录且不是使用微信登录
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("phone",phone);
            UserInfo userInfo = baseMapper.selectOne(queryWrapper);
            if(userInfo == null){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                baseMapper.insert(userInfo);
                userInfo.setStatus(0);
            }
            map = getMap(userInfo);
        }else{
            //用户为微信登录，此时需要把手机号和微信合二为一
            UserInfo userInfoFinal = new UserInfo();
            //根据手机获取用户信息
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("phone",phone);
            UserInfo userInfoByPhone = baseMapper.selectOne(queryWrapper);
            if(userInfoByPhone != null){
                //把获取的手机号赋值给最终的userInfo
                BeanUtils.copyProperties(userInfoByPhone,userInfoFinal);
                //删除该手机号对应的记录
                this.remove(new QueryWrapper<UserInfo>().eq("phone",phone));
            }
            //根据openid查找对应的信息
            UserInfo userInfoByOpenId = baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("openid", openid));
            userInfoFinal.setOpenid(userInfoByOpenId.getOpenid());
            userInfoFinal.setId(userInfoByOpenId.getId());
            userInfoFinal.setNickName("Andy");
            if(userInfoByPhone == null){
                userInfoFinal.setPhone(phone);
                userInfoFinal.setStatus(userInfoByOpenId.getStatus());
            }
            //修改手机号
            baseMapper.updateById(userInfoFinal);
            map = getMap(userInfoFinal);
        }
        return map;
    }
    public Map<String,Object> getMap(UserInfo userInfo){
        if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"该用户已锁定，无法使用");
        }
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        map.put("name",name);
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());
        map.put("token",token);
        return map;
    }
    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return userInfo;
    }

    @Override
    public void saveUserAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        //证件号码
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        //证件类型
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        //证件地址
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        //姓名
        userInfo.setName(userAuthVo.getName());
        //把状态改为1
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        userInfo.setStatus(1);
        baseMapper.updateById(userInfo);
    }

    @Override
    public Page<UserInfo> selectPage(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo) {

        Page<UserInfo> userInfoPage = new Page<UserInfo>(pageNum, limit);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        Integer status = userInfoQueryVo.getStatus();
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        String name = userInfoQueryVo.getKeyword();
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)){
            queryWrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.eq("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.eq("create_time",createTimeEnd);
        }
        Page<UserInfo> page = this.page(userInfoPage, queryWrapper);
        //设置用户的状态
        page.getRecords().stream().forEach(userInfo -> {
            this.setUserInfoByStatus(userInfo);
        });
        return page;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        return this.updateById(userInfo);
    }

    @Override
    public Map<String, Object> getAllPatient(Long id) {
        Map<String, Object> map = new HashMap<>();
        UserInfo userInfo = baseMapper.selectById(id);
        this.setUserInfoByStatus(userInfo);
        map.put("userInfo",userInfo);
        List<Patient> patientList = patientService.getAllPatient(id);
        map.put("patientList",patientList);
        return map;
    }

    @Override
    public boolean updateAuthStatus(Long id, Integer authStatus) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAuthStatus(authStatus);
        return this.updateById(userInfo);
    }

    //根据用户的状态设置用户信息
    public void setUserInfoByStatus(UserInfo userInfo){
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
    }


}
