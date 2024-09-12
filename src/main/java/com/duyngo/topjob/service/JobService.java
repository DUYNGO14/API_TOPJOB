package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.job.ResJobCreateDTO;
import com.duyngo.topjob.domain.response.job.ResJobDTO;
import com.duyngo.topjob.domain.response.job.ResJobUpdateDTO;
import com.duyngo.topjob.exception.JobException;

public interface JobService {
    public ResJobCreateDTO create(Job job) throws JobException;

    public ResJobUpdateDTO update(Job job) throws JobException;

    public void delete(long id) throws JobException;

    public ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable);

    public Job getJobById(long id);

    public ResJobDTO convertResJobDTO(Job job);

    public ResJobCreateDTO convertResJobCreateDTO(Job job);

    public ResJobUpdateDTO convertResJobUpdateDTO(Job job);
}
