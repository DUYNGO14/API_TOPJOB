package com.duyngo.topjob.controller.admin;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AdminController {
    @GetMapping("/home")
    public String getMethodName() {
        return "hrllll";
    }

}
