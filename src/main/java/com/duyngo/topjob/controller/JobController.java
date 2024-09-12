package com.duyngo.topjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.job.ResJobCreateDTO;
import com.duyngo.topjob.domain.response.job.ResJobDTO;
import com.duyngo.topjob.domain.response.job.ResJobUpdateDTO;
import com.duyngo.topjob.exception.JobException;
import com.duyngo.topjob.exception.SkillException;
import com.duyngo.topjob.service.JobService;
import com.duyngo.topjob.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Get job by id")
    public ResponseEntity<ResJobDTO> getJobById(@PathVariable("id") long id) throws JobException {
        Job job = this.jobService.getJobById(id);
        if (job == null) {
            throw new JobException("Job with id = " + id + " not exists!");
        }
        return ResponseEntity.ok().body(this.jobService.convertResJobDTO(job));
    }

    @GetMapping()
    @ApiMessage("Get all job")
    public ResponseEntity<ResultPaginationDTO> getAll(@Filter Specification<Job> spec, Pageable pageable)
            throws JobException {
        return ResponseEntity.ok().body(this.jobService.getAllJob(spec, pageable));
    }

    @PostMapping()
    @ApiMessage("Create new job!")
    public ResponseEntity<ResJobCreateDTO> create(@RequestBody Job request) throws JobException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(request));
    }

    @PutMapping()
    @ApiMessage("Update new job!")
    public ResponseEntity<ResJobUpdateDTO> update(@RequestBody Job request) throws JobException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.update(request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete new job!")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws JobException {
        this.jobService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
