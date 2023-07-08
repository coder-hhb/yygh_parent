package com.hhb.yygh.user.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hhb.yygh.model.user.UserInfo;
import com.hhb.yygh.vo.user.LoginVo;
import com.hhb.yygh.vo.user.UserAuthVo;
import com.hhb.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hhb
 * @since 2023-06-10
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo getUserInfo(Long userId);

    void saveUserAuth(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> selectPage(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo);

    boolean updateStatus(Long id, Integer status);

    Map<String, Object> getAllPatient(Long id);

    boolean updateAuthStatus(Long id, Integer authStatus);
}
