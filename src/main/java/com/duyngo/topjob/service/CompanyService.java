package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.CompanyException;

public interface CompanyService {
    public Company createCompany(Company company) throws CompanyException;

    public Company updateCompany(Company company) throws CompanyException;

    public Company getCompanyById(long id);

    public ResultPaginationDTO getAll(Specification<Company> spec, Pageable pageable);

    public void deleteCompany(long id) throws CompanyException;

    public boolean isExistCompany(String name);

}
