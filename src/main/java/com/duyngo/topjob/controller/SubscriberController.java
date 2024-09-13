package com.duyngo.topjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Subscriber;
import com.duyngo.topjob.domain.response.subscrice.ResSubscribeDTO;
import com.duyngo.topjob.exception.SubscriberException;
import com.duyngo.topjob.service.SubscriberService;
import com.duyngo.topjob.util.annotation.ApiMessage;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping()
    @ApiMessage("Create subscriber success!")
    public ResponseEntity<ResSubscribeDTO> create(@Valid @RequestBody Subscriber request) throws SubscriberException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(request));
    }

    @PutMapping()
    @ApiMessage("Update subscriber success!")
    public ResponseEntity<ResSubscribeDTO> update(@Valid @RequestBody Subscriber request) throws SubscriberException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.update(request));
    }

}
