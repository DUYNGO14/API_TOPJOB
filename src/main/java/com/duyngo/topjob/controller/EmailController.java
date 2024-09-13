package com.duyngo.topjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.service.EmailService;
import com.duyngo.topjob.service.SubscriberService;
import com.duyngo.topjob.util.annotation.ApiMessage;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    // @Scheduled(cron = "*/40 * * * * *")
    @Transactional
    public String sendEmail() {
        System.out.println("Gửi mail!");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }

}
