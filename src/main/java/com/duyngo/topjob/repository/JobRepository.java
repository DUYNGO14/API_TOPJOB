package com.duyngo.topjob.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Optional<Job> findById(long id);

    boolean existsByNameAndCompany(String name, Company company);

    List<Job> findBySkillsIn(List<Skill> listSkills);
}
