package com.icckevin.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: 注册
 * @author: iccKevin
 * @create: 2020-05-16 10:31
 **/
@Controller
public class LoginController {
    @RequestMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }
}