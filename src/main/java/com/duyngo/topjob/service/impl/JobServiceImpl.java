package com.duyngo.topjob.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.job.ResJobCreateDTO;
import com.duyngo.topjob.domain.response.job.ResJobDTO;
import com.duyngo.topjob.domain.response.job.ResJobUpdateDTO;
import com.duyngo.topjob.exception.JobException;
import com.duyngo.topjob.repository.CompanyRepository;
import com.duyngo.topjob.repository.JobRepository;
import com.duyngo.topjob.repository.SkillRepository;
import com.duyngo.topjob.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobServiceImpl(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public ResJobCreateDTO create(Job job) throws JobException {
        boolean isExistJob = this.jobRepository.existsByNameAndCompany(job.getName(), job.getCompany());
        if (isExistJob) {
            throw new JobException("Job is exists!");
        }
        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(job.getCompany().getId());
            if (cOptional.isPresent()) {
                job.setCompany(cOptional.get());
            }
        }
        Job currentJob = this.jobRepository.save(job);
        return this.convertResJobCreateDTO(currentJob);

    }

    @Override
    public ResJobUpdateDTO update(Job job) throws JobException {
        Job currentJob = this.getJobById(job.getId());
        if (currentJob == null) {
            throw new JobException("Job with id = " + job.getId() + "not exists!");
        }
        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            currentJob.setSkills(dbSkills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(job.getCompany().getId());
            if (cOptional.isPresent()) {
                currentJob.setCompany(cOptional.get());
            }
        }

        // update correct info
        currentJob.setName(job.getName());
        currentJob.setSalary(job.getSalary());
        currentJob.setQuantity(job.getQuantity());
        currentJob.setLocation(job.getLocation());
        currentJob.setLevel(job.getLevel());
        currentJob.setStartDate(job.getStartDate());
        currentJob.setEndDate(job.getEndDate());
        currentJob.setActive(job.isActive());

        // update job
        currentJob = this.jobRepository.save(currentJob);
        return this.convertResJobUpdateDTO(currentJob);

    }

    @Override
    public void delete(long id) throws JobException {
        Job currentJob = this.getJobById(id);
        if (currentJob == null) {
            throw new JobException("Job with id = " + id + "not exists!");
        }
        this.jobRepository.delete(currentJob);
    }

    @Override
    public ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(jobPage.getTotalPages())
                .total(jobPage.getTotalElements())
                .build();
        List<ResJobDTO> jobDTOs = jobPage.getContent()
                .stream()
                .map(item -> this.convertResJobDTO(item))
                .collect(Collectors.toList());
        return ResultPaginationDTO.builder().meta(mt).result(jobDTOs).build();

    }

    @Override
    public Job getJobById(long id) {
        Optional<Job> job = this.jobRepository.findById(id);
        return job.isPresent() ? job.get() : null;
    }

    @Override
    public ResJobDTO convertResJobDTO(Job job) {
        List<ResJobDTO.SkillJob> skillJobs = new ArrayList<ResJobDTO.SkillJob>();
        List<Skill> skills = job.getSkills();
        if (skills.size() != 0) {
            for (Skill s : skills) {
                ResJobDTO.SkillJob skill = ResJobDTO.SkillJob
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .build();
                skillJobs.add(skill);
            }
        }
        return ResJobDTO.builder().id(job.getId())
                .name(job.getName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .quantity(job.getQuantity())
                .level(job.getLevel())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .isActive(job.isActive())
                .createdAt(job.getCreatedAt())
                .createdBy(job.getCreatedBy())
                .updatedAt(job.getUpdatedAt())
                .updatedBy(job.getUpdatedBy())
                .skills(skillJobs)
                .build();
    }

    @Override
    public ResJobCreateDTO convertResJobCreateDTO(Job job) {
        List<ResJobCreateDTO.SkillJob> skillJobs = new ArrayList<ResJobCreateDTO.SkillJob>();
        List<Skill> skills = job.getSkills();
        if (skills.size() != 0) {
            for (Skill s : skills) {
                ResJobCreateDTO.SkillJob skill = ResJobCreateDTO.SkillJob
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .build();
                skillJobs.add(skill);
            }
        }
        return ResJobCreateDTO.builder().id(job.getId())
                .name(job.getName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .quantity(job.getQuantity())
                .level(job.getLevel())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .isActive(job.isActive())
                .createdAt(job.getCreatedAt())
                .createdBy(job.getCreatedBy())
                .skills(skillJobs)
                .build();
    }

    @Override
    public ResJobUpdateDTO convertResJobUpdateDTO(Job job) {
        List<ResJobUpdateDTO.SkillJob> skillJobs = new ArrayList<ResJobUpdateDTO.SkillJob>();
        List<Skill> skills = job.getSkills();
        if (skills.size() != 0) {
            for (Skill s : skills) {
                ResJobUpdateDTO.SkillJob skill = ResJobUpdateDTO.SkillJob
                        .builder()
                        .id(s.getId())
                        .name(s.getName())
                        .build();
                skillJobs.add(skill);
            }
        }
        return ResJobUpdateDTO.builder().id(job.getId())
                .name(job.getName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .quantity(job.getQuantity())
                .level(job.getLevel())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .isActive(job.isActive())
                .updatedAt(job.getUpdatedAt())
                .updatedBy(job.getUpdatedBy())
                .skills(skillJobs)
                .build();
    }

}
