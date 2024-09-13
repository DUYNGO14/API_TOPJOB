package com.duyngo.topjob.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.SkillException;
import com.duyngo.topjob.repository.SkillRepository;
import com.duyngo.topjob.service.SkillService;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill create(Skill skill) throws SkillException {
        boolean isExistsSkill = this.skillRepository.existsByName(skill.getName());
        if (isExistsSkill) {
            throw new SkillException("Skill with name = " + skill.getName() + "exists!");
        }
        return this.skillRepository.save(skill);
    }

    @Override
    public Skill update(Skill skill) throws SkillException {
        Skill currentSkill = this.getSkillById(skill.getId());
        if (currentSkill == null) {
            throw new SkillException("Skill with id = " + skill.getId() + "exists!");
        }
        if (!currentSkill.getName().equals(skill.getName())) {
            if (this.skillRepository.existsByName(skill.getName())) {
                throw new SkillException("Skill with name = " + skill.getName() + "exists!");
            }
        }

        currentSkill.setName(skill.getName());
        return this.skillRepository.save(currentSkill);
    }

    @Override
    public Skill getSkillById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        return skill.isPresent() ? skill.get() : null;
    }

    @Override
    public ResultPaginationDTO getAll(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skillPage = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta
                .builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(skillPage.getTotalPages())
                .total(skillPage.getTotalElements())
                .build();

        return ResultPaginationDTO.builder().meta(mt).result(skillPage.getContent()).build();
    }

    @Override
    public void delete(long id) throws SkillException {
        Skill currentSkill = this.getSkillById(id);
        if (currentSkill == null) {
            throw new SkillException("Skill with id = " + id + " exists!");
        }
        currentSkill.getJobs().forEach(e -> e.getSkills().remove(currentSkill));

        currentSkill.getSubscribers().forEach(s -> s.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);
        // currentSkill.setDeleted(true);
        // this.skillRepository.save(currentSkill);
    }

}
