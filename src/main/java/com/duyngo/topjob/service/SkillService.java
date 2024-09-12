package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.SkillException;

public interface SkillService {
    public Skill create(Skill skill) throws SkillException;

    public Skill update(Skill skill) throws SkillException;

    public Skill getSkillById(long id);

    public ResultPaginationDTO getAll(Specification<Skill> spec, Pageable pageable);

    public void delete(long id) throws SkillException;
}
