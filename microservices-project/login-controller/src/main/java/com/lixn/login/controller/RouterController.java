package com.lixn.login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/22
 * @描述
 */
@RestController
public class RouterController {

    @GetMapping("login.html")
    public String login(){
        return "login";
    }

    @GetMapping("index.html")
    public String index(){
        return "index";
    }

    @GetMapping("/")
    public String _login(){
        return "login";
    }
}
