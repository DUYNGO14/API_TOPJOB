package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.user_request.ReqUserUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.user.ResCreateUserDTO;
import com.duyngo.topjob.domain.response.user.ResUpdateUserDTO;
import com.duyngo.topjob.domain.response.user.ResUserDTO;
import com.duyngo.topjob.exception.CheckInvalidException;

public interface UserService {
    public User createUSer(User user) throws CheckInvalidException;

    public User getUserById(long id);

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable);

    public User updateUser(ReqUserUpdateDTO reqUser) throws CheckInvalidException;

    public void deleteUser(long id) throws CheckInvalidException;

    public ResCreateUserDTO convertResCreateUserDTO(User user);

    public ResUpdateUserDTO convertResUpdateUserDTO(User user);

    public ResUserDTO convertResUserDTO(User user);

    public User getUserByEmail(String email);

}
