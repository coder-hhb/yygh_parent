package com.hhb.yygh.hosp.controller.admin;


import com.hhb.yygh.common.config.result.R;
import com.hhb.yygh.model.acl.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @PostMapping("/login")
    public R loginUser(@RequestBody User user){
        return R.ok().data("token","admin-token");
    }
    @GetMapping("/info")
    public R info(String token){
        return R.ok().data("roles","[admin]")
                .data("introduction","I am a super administrator")
                .data("avatar","https://pica.zhimg.com/80/v2-3e709fd60a23ab7fc15c6e95a0aeec34_720w.webp?source=1940ef5c")
                .data("name","Super Admin");
    }
}
