package com.duyngo.topjob.service;

import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.Subscriber;
import com.duyngo.topjob.domain.response.email.ResEmailJob;
import com.duyngo.topjob.domain.response.subscrice.ResSubscribeDTO;
import com.duyngo.topjob.exception.SubscriberException;

public interface SubscriberService {
    public ResSubscribeDTO create(Subscriber subscriber) throws SubscriberException;

    public ResSubscribeDTO update(Subscriber subscriber) throws SubscriberException;

    public Subscriber getById(long id);

    public ResSubscribeDTO convertSubscribeDTO(Subscriber subscriber);

    public ResEmailJob convertJobToSendEmail(Job job);

    public void sendSubscribersEmailJobs();
}
