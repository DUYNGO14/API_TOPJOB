package com.duyngo.topjob.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.CompanyException;
import com.duyngo.topjob.repository.CompanyRepository;
import com.duyngo.topjob.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company createCompany(Company company) throws CompanyException {
        boolean isExistCompany = this.isExistCompany(company.getName());
        if (isExistCompany) {
            throw new CompanyException("Company with name exists!");
        }
        return this.companyRepository.save(company);
    }

    @Override
    public Company updateCompany(Company company) throws CompanyException {
        Company currentCompany = this.getCompanyById(company.getId());
        if (currentCompany == null) {
            throw new CompanyException("Company with id : " + company.getId() + " not exists!");
        }
        if (!currentCompany.getName().equals(company.getName())) {
            boolean isExistCompany = this.isExistCompany(company.getName());
            if (isExistCompany) {
                throw new CompanyException("Company with name exists!");
            }
        }

        currentCompany.setName(company.getName());
        currentCompany.setAddress(company.getAddress());
        currentCompany.setDescription(company.getDescription());
        currentCompany.setLogo(company.getLogo());
        return this.companyRepository.save(currentCompany);
    }

    @Override
    public Company getCompanyById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        return company.isPresent() ? company.get() : null;
    }

    @Override
    public ResultPaginationDTO getAll(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(pageCompany.getTotalPages())
                .total(pageCompany.getTotalElements())
                .build();
        return ResultPaginationDTO.builder().meta(mt).result(pageCompany.getContent()).build();
    }

    @Override
    public void deleteCompany(long id) throws CompanyException {
        Company currentCompany = this.getCompanyById(id);
        if (currentCompany == null) {
            throw new CompanyException("Company with id : " + id + " not exists!");
        }
        currentCompany.setDeleted(true);
        this.companyRepository.save(currentCompany);
    }

    @Override
    public boolean isExistCompany(String name) {
        return this.companyRepository.existsByName(name);
    }

}
