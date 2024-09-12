package com.duyngo.topjob.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.CompanyException;
import com.duyngo.topjob.service.CompanyService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Get company by id")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) throws CompanyException {
        Company company = this.companyService.getCompanyById(id);
        if (company == null) {
            throw new CompanyException("Company with id : " + id + " not exists!");
        }
        return ResponseEntity.ok(company);
    }

    @GetMapping()
    @ApiMessage("Get all company")
    public ResponseEntity<ResultPaginationDTO> getAll(@Filter Specification<Company> spec, Pageable pageable)
            throws CompanyException {
        return ResponseEntity.ok(this.companyService.getAll(spec, pageable));
    }

    @PostMapping()
    @ApiMessage("Create new company!")
    public ResponseEntity<Company> create(@RequestBody Company request) throws CompanyException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.createCompany(request));
    }

    @PutMapping()
    @ApiMessage("Update company")
    public ResponseEntity<Company> update(@RequestBody Company request) throws CompanyException {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.updateCompany(request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete company")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws CompanyException {
        this.companyService.deleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
