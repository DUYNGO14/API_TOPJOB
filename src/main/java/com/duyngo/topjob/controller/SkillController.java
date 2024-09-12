package com.duyngo.topjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Skill;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.SkillException;
import com.duyngo.topjob.service.SkillService;
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
@RequestMapping("/api/v1/skills")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Get skill by id")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) throws SkillException {
        Skill skill = this.skillService.getSkillById(id);
        if (skill == null) {
            throw new SkillException("Skill with id = " + id + " not exists!");
        }

        return ResponseEntity.ok().body(skill);
    }

    @GetMapping()
    @ApiMessage("Get all skill")
    public ResponseEntity<ResultPaginationDTO> getAll(@Filter Specification<Skill> spec, Pageable pageable)
            throws SkillException {
        return ResponseEntity.ok().body(this.skillService.getAll(spec, pageable));
    }

    @PostMapping()
    @ApiMessage("Create new skill!")
    public ResponseEntity<Skill> create(@RequestBody Skill request) throws SkillException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.create(request));
    }

    @PutMapping()
    @ApiMessage("Update new skill!")
    public ResponseEntity<Skill> update(@RequestBody Skill request) throws SkillException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.update(request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete skill!")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws SkillException {
        this.skillService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
