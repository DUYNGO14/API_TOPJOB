package com.duyngo.topjob.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.Job;
import com.duyngo.topjob.domain.Resume;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.resume_request.ReqResumeUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.resume.ResCreateResumeDTO;
import com.duyngo.topjob.domain.response.resume.ResResumeDTO;
import com.duyngo.topjob.domain.response.resume.ResUpdateResumeDTO;
import com.duyngo.topjob.exception.ResumeException;
import com.duyngo.topjob.repository.JobRepository;
import com.duyngo.topjob.repository.ResumeRepository;
import com.duyngo.topjob.repository.UserRepository;
import com.duyngo.topjob.service.ResumeService;
import com.duyngo.topjob.util.SecurityUtil;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;
    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeServiceImpl(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public ResCreateResumeDTO create(Resume resume) throws ResumeException {
        boolean isIdExist = this.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new ResumeException("Job id/User id not exists!");
        }
        Resume re = this.resumeRepository.save(resume);
        return this.convertResCreateResumeDTO(re);
    }

    @Override
    public ResUpdateResumeDTO update(ReqResumeUpdateDTO request) throws ResumeException {
        Resume currentResume = this.getResumeById(request.getId());
        if (currentResume == null) {
            throw new ResumeException("Resume does not exists!");
        }
        currentResume.setStatus(request.getStatus());
        currentResume = this.resumeRepository.save(currentResume);
        return this.convertResUpdateResumeDTO(currentResume);
    }

    @Override
    public Resume getResumeById(long id) {
        Optional<Resume> currentResume = this.resumeRepository.findById(id);
        return currentResume.isPresent() ? currentResume.get() : null;
    }

    @Override
    public ResultPaginationDTO getAll(Specification<Resume> spec, Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userRepository.findByEmail(email).get();
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(fb.field("job")
                .in(fb.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta.builder().page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(resumePage.getTotalPages())
                .total(resumePage.getTotalElements())
                .build();
        List<ResResumeDTO> listResume = resumePage.getContent().stream().map(e -> this.convertResResumeDTO(e))
                .collect(Collectors.toList());
        return ResultPaginationDTO.builder().meta(mt).result(listResume).build();
    }

    @Override
    public void delete(long id) throws ResumeException {
        Resume currentResume = this.getResumeById(id);
        if (currentResume == null) {
            throw new ResumeException("Resume does not exists!");
        }
        this.resumeRepository.delete(currentResume);
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User user = this.userRepository.findByEmail(email).get();
        FilterNode node = filterParser.parse("email='" + user.getEmail() + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta
                .builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(resumePage.getTotalPages())
                .total(resumePage.getTotalElements())
                .build();
        List<ResResumeDTO> listResume = resumePage.getContent().stream()
                .map(e -> this.convertResResumeDTO(e))
                .collect(Collectors.toList());
        return ResultPaginationDTO.builder().meta(mt).result(listResume).build();
    }

    @Override
    public ResCreateResumeDTO convertResCreateResumeDTO(Resume resume) {
        return ResCreateResumeDTO.builder()
                .id(resume.getId())
                .email(resume.getEmail())
                .url(resume.getUrl())
                .status(resume.getStatus())
                .createdAt(resume.getCreatedAt())
                .createdBy(resume.getCreatedBy())
                .build();
    }

    @Override
    public ResUpdateResumeDTO convertResUpdateResumeDTO(Resume resume) {
        return ResUpdateResumeDTO.builder()
                .id(resume.getId())
                .email(resume.getEmail())
                .url(resume.getUrl())
                .status(resume.getStatus())
                .updatedAt(resume.getUpdatedAt())
                .updatedBy(resume.getUpdatedBy())
                .build();
    }

    @Override
    public ResResumeDTO convertResResumeDTO(Resume resume) {
        ResResumeDTO.UserResume userResume = new ResResumeDTO.UserResume();
        ResResumeDTO.JobResume jobResume = new ResResumeDTO.JobResume();
        if (resume.getUser() != null) {
            userResume = ResResumeDTO.UserResume.builder().id(resume.getUser().getId())
                    .name(resume.getUser().getFullname()).build();
        }

        if (resume.getJob() != null) {
            jobResume = ResResumeDTO.JobResume.builder().id(resume.getJob().getId()).name(resume.getJob().getName())
                    .build();
        }
        return ResResumeDTO.builder()
                .id(resume.getId())
                .email(resume.getEmail())
                .url(resume.getUrl())
                .status(resume.getStatus())
                .createdAt(resume.getCreatedAt())
                .createdBy(resume.getCreatedBy())
                .updatedAt(resume.getUpdatedAt())
                .updatedBy(resume.getUpdatedBy())
                .companyName(resume.getJob().getCompany().getName())
                .user(userResume)
                .job(jobResume)
                .build();
    }

    @Override
    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }

}
