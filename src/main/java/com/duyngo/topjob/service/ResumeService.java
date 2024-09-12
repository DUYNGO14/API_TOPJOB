package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.Resume;
import com.duyngo.topjob.domain.request.resume_request.ReqResumeUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.resume.ResCreateResumeDTO;
import com.duyngo.topjob.domain.response.resume.ResResumeDTO;
import com.duyngo.topjob.domain.response.resume.ResUpdateResumeDTO;
import com.duyngo.topjob.exception.ResumeException;

public interface ResumeService {
    public ResCreateResumeDTO create(Resume resume) throws ResumeException;

    public ResUpdateResumeDTO update(ReqResumeUpdateDTO resume) throws ResumeException;

    public Resume getResumeById(long id);

    public ResultPaginationDTO getAll(Specification<Resume> spec, Pageable pageable);

    public void delete(long id) throws ResumeException;

    public boolean checkResumeExistByUserAndJob(Resume resume);

    public ResCreateResumeDTO convertResCreateResumeDTO(Resume resume);

    public ResUpdateResumeDTO convertResUpdateResumeDTO(Resume resume);

    public ResResumeDTO convertResResumeDTO(Resume resume);

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable);
}
