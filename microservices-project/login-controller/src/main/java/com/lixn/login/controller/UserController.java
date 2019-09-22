package com.lixn.login.controller;

import com.lixn.login.POJO.ApiResult;
import com.lixn.login.POJO.CurrentUser;
import com.lixn.login.POJO.UserBO;
import com.lixn.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/22
 * @描述
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public ApiResult login(@RequestBody UserBO user){
        return new ApiResult(200,"登陆成功",userService.buildUserInfo(user));
    }

    @GetMapping("user/info")
    public ApiResult info() {
        return new ApiResult(200, null, CurrentUser.get());
    }

    @PostMapping("logout")
    public ApiResult logout(@RequestHeader("Authorization") String jwt) {
        userService.logout(jwt);
        return new ApiResult(200, "成功", null);
    }
}
