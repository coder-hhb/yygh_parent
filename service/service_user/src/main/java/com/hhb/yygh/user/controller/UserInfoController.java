package com.hhb.yygh.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.model.user.UserInfo;
import com.hhb.yygh.user.service.UserInfoService;
import com.hhb.yygh.user.utils.JwtHelper;
import com.hhb.yygh.vo.user.LoginVo;
import com.hhb.yygh.vo.user.UserAuthVo;
import com.hhb.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author hhb
 * @since 2023-06-10
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){

        Map<String,Object> map = userInfoService.login(loginVo);
        return R.ok().data(map);
    }

    @GetMapping("/info")
    //根据传过来的token获取用户信息
    public R getUserInfo(@RequestHeader("token") String token){
        Long userId = JwtHelper.getUserId(token);
        System.out.println("token :" + token);
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        return R.ok().data("userInfo",userInfo);
    }
    @GetMapping("/test")
    public R test(){
        String str = "Str";
        return R.ok().data("String",str);
    }

    //修改用户信息
    @PutMapping("/update")
    public R saveUserInfo(@RequestHeader String token, UserAuthVo userAuthVo){
        Long userId = JwtHelper.getUserId(token);
        userInfoService.saveUserAuth(userId,userAuthVo);
        return R.ok();
    }

    //用户分类列表
    @GetMapping("/{pageNum}/{limit}")
    public R pageList(@PathVariable Integer pageNum,
                      @PathVariable Integer limit,
                      UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> page = userInfoService.selectPage(pageNum,limit,userInfoQueryVo);
        return R.ok().data("rows",page.getRecords()).data("total",page.getTotal());
    }

    //用户锁定与解锁
    @PostMapping("/lock/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        return userInfoService.updateStatus(id,status) ? R.ok() : R.error();
    }

    //用户认证
    @PostMapping("/pass/{id}/{authStatus}")
    public R updateAuthStatus(@PathVariable Long id,@PathVariable Integer authStatus){
        return userInfoService.updateAuthStatus(id,authStatus) ? R.ok() : R.error();
    }

    //查看用户纠正人信息
    @GetMapping("/show/{id}")
    public R showPathentByUserId(@PathVariable Long id){
        Map<String,Object> map = userInfoService.getAllPatient(id);
        return R.ok().data(map);
    }
}

