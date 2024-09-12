package com.duyngo.topjob.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Resume;

import com.duyngo.topjob.domain.request.resume_request.ReqResumeUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.resume.ResCreateResumeDTO;
import com.duyngo.topjob.domain.response.resume.ResResumeDTO;
import com.duyngo.topjob.domain.response.resume.ResUpdateResumeDTO;
import com.duyngo.topjob.exception.ResumeException;
import com.duyngo.topjob.service.ResumeService;

import com.duyngo.topjob.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResResumeDTO> fetchById(@PathVariable("id") long id) throws ResumeException {
        Resume reqResumeOptional = this.resumeService.getResumeById(id);
        if (reqResumeOptional == null) {
            throw new ResumeException("Resume with id = " + id + " not exists!");
        }

        return ResponseEntity.ok().body(this.resumeService.convertResResumeDTO(reqResumeOptional));
    }

    @GetMapping()
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.getAll(spec, pageable));
    }

    @PostMapping()
    @ApiMessage("Create new resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume request) throws ResumeException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(request));
    }

    @PutMapping()
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody ReqResumeUpdateDTO request) throws ResumeException {
        return ResponseEntity.ok(this.resumeService.update(request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete resume by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws ResumeException {
        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
