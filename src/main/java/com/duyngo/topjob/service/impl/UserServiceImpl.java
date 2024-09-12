package com.duyngo.topjob.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.Role;
import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.user_request.ReqUserUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.user.ResCreateUserDTO;
import com.duyngo.topjob.domain.response.user.ResUpdateUserDTO;
import com.duyngo.topjob.domain.response.user.ResUserDTO;
import com.duyngo.topjob.exception.CheckInvalidException;
import com.duyngo.topjob.repository.UserRepository;
import com.duyngo.topjob.service.CompanyService;
import com.duyngo.topjob.service.RoleService;
import com.duyngo.topjob.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder,
            CompanyService companyService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
    }

    @Override
    public User createUSer(User user) throws CheckInvalidException {
        boolean isExistsEmail = this.userRepository.existsByEmail(user.getEmail());
        if (isExistsEmail) {
            throw new CheckInvalidException("Email " + user.getEmail() + " existed!");
        }
        // check role
        if (user.getRole() != null) {
            Role r = this.roleService.getRoleById(user.getRole().getId());
            user.setRole(r);
        }
        // check company
        if (user.getCompany() != null) {
            Company c = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(c);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {

        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        // remove sensitive data
        List<ResUserDTO> listUserDto = pageUser.getContent()
                .stream().map(item -> this.convertResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(listUserDto);
        return rs;
    }

    @Override
    public User updateUser(ReqUserUpdateDTO reqUser) throws CheckInvalidException {
        User currentUser = this.getUserById(reqUser.getId());
        if (currentUser == null) {
            throw new CheckInvalidException("User with id: " + reqUser.getId() + " not exist!");
        }
        currentUser.setFullname(reqUser.getFullname());
        currentUser.setAddress(reqUser.getAddress());
        currentUser.setUsername(reqUser.getUsername());
        currentUser.setPhonenumber(reqUser.getPhonenumber());
        // check role
        if (reqUser.getRole() != null) {
            Role r = this.roleService.getRoleById(reqUser.getRole().getId());
            currentUser.setRole(r);
        }
        // check company
        if (reqUser.getCompany() != null) {
            Company c = this.companyService.getCompanyById(reqUser.getCompany().getId());
            currentUser.setCompany(c);
        }
        return this.userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(long id) throws CheckInvalidException {
        User user = this.getUserById(id);
        if (user == null) {
            throw new CheckInvalidException("User with id: " + id + " not exist!");
        }
        user.setDeleted(true);
        this.userRepository.save(user);
        // this.userRepository.deleteById(id);
    }

    @Override
    public ResCreateUserDTO convertResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.RoleUser role = new ResCreateUserDTO.RoleUser();
        ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser();
        if (user.getRole() != null) {
            role = ResCreateUserDTO.RoleUser.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName())
                    .build();
        }
        if (user.getCompany() != null) {
            company = ResCreateUserDTO.CompanyUser.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .build();
        }
        res = ResCreateUserDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phonenumber(user.getPhonenumber())
                .address(user.getAddress())
                .createAt(user.getCreatedAt())
                .createBy(user.getCreatedBy())
                .role(role)
                .company(company)
                .build();
        return res;
    }

    @Override
    public ResUpdateUserDTO convertResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.RoleUser role = new ResUpdateUserDTO.RoleUser();
        ResUpdateUserDTO.CompanyUser company = new ResUpdateUserDTO.CompanyUser();
        if (user.getRole() != null) {
            role = ResUpdateUserDTO.RoleUser.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName())
                    .build();
        }
        if (user.getCompany() != null) {
            company = ResUpdateUserDTO.CompanyUser.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .build();
        }
        res = ResUpdateUserDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phonenumber(user.getPhonenumber())
                .address(user.getAddress())
                .updateAt(user.getCreatedAt())
                .updateBy(user.getCreatedBy())
                .role(role)
                .company(company)
                .build();
        return res;
    }

    @Override
    public ResUserDTO convertResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();
        ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
        if (user.getRole() != null) {
            role = ResUserDTO.RoleUser.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName())
                    .build();
        }
        if (user.getCompany() != null) {
            company = ResUserDTO.CompanyUser.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .build();
        }
        res = ResUserDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .phonenumber(user.getPhonenumber())
                .address(user.getAddress())
                .deleted(user.isDeleted())
                .updateAt(user.getCreatedAt())
                .updateBy(user.getCreatedBy())
                .createAt(user.getCreatedAt())
                .createBy(user.getCreatedBy())
                .role(role)
                .company(company)
                .build();
        return res;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

}
