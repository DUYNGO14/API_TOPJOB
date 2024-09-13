package com.duyngo.topjob.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.Subscriber;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.response.email.ResEmailJob;
import com.duyngo.topjob.domain.response.subscrice.ResSubscribeDTO;
import com.duyngo.topjob.exception.SubscriberException;
import com.duyngo.topjob.repository.JobRepository;
import com.duyngo.topjob.repository.SkillRepository;
import com.duyngo.topjob.repository.SubscriberRepository;
import com.duyngo.topjob.repository.UserRepository;
import com.duyngo.topjob.service.EmailService;
import com.duyngo.topjob.service.SubscriberService;
import com.duyngo.topjob.util.SecurityUtil;

@Service
public class SubscriberServiceImpl implements SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository, UserRepository userRepository,
            SkillRepository skillRepository, EmailService emailService, JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    @Override
    public ResSubscribeDTO create(Subscriber subscriber) throws SubscriberException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User user = new User();
        if (email != null) {
            user = this.userRepository.findByEmail(email).get();
        }
        boolean isExistsUser = this.subscriberRepository.existsByUser(user);
        if (isExistsUser) {
            throw new SubscriberException("User already exists!");
        }
        subscriber.setUser(user);
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkill = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkill);
        }
        subscriber = this.subscriberRepository.save(subscriber);
        return this.convertSubscribeDTO(subscriber);

    }

    @Override
    public ResSubscribeDTO update(Subscriber subscriber) throws SubscriberException {
        Subscriber currentSubscriber = this.getById(subscriber.getId());
        if (currentSubscriber == null) {
            throw new SubscriberException("User does not exist!");
        }
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkill = this.skillRepository.findByIdIn(reqSkills);
            currentSubscriber.setSkills(dbSkill);
        }
        currentSubscriber = this.subscriberRepository.save(currentSubscriber);
        return this.convertSubscribeDTO(currentSubscriber);
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                        List<ResEmailJob> arr = listJobs.stream().map(job -> this.convertJobToSendEmail(job))
                                .collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getUser().getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getUser().getFullname(),
                                arr);
                    }
                }
            }
        }
    }

    @Override
    public ResSubscribeDTO convertSubscribeDTO(Subscriber subscriber) {
        ResSubscribeDTO.UserSub userSub = new ResSubscribeDTO.UserSub();
        List<ResSubscribeDTO.SkillSub> listSkillSub = new ArrayList<ResSubscribeDTO.SkillSub>();
        ResSubscribeDTO.SkillSub skillSub = new ResSubscribeDTO.SkillSub();
        if (subscriber.getUser() != null) {
            userSub = ResSubscribeDTO.UserSub.builder()
                    .id(subscriber.getUser().getId())
                    .email(subscriber.getUser().getEmail())
                    .fullname(subscriber.getUser().getFullname())
                    .build();
        }
        if (subscriber.getSkills().size() != 0) {
            for (Skill skill : subscriber.getSkills()) {
                skillSub.setId(skill.getId());
                skillSub.setName(skill.getName());
                listSkillSub.add(skillSub);
            }
        }
        return ResSubscribeDTO.builder().id(subscriber.getId()).user(userSub).skill(listSkillSub).build();
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    @Override
    public Subscriber getById(long id) {
        Optional<Subscriber> currentSubscriber = this.subscriberRepository.findById(id);
        return currentSubscriber.isPresent() ? currentSubscriber.get() : null;
    }

}
