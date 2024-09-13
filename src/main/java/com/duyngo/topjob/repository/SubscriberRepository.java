package com.duyngo.topjob.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.Subscriber;
import com.duyngo.topjob.domain.User;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    boolean existsByUser(User user);

    List<Skill> findByIdIn(List<Long> reqSkills);

}
