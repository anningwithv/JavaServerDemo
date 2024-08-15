package com.v.game.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @RequestMapping("/login")
    public String login()
    {
        //调用业务，接收业务返回
        return "Login success";
    }
}
