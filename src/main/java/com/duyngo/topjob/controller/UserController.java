package com.duyngo.topjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.User;
import com.duyngo.topjob.domain.request.user_request.ReqUserUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.user.ResCreateUserDTO;
import com.duyngo.topjob.domain.response.user.ResUpdateUserDTO;
import com.duyngo.topjob.domain.response.user.ResUserDTO;
import com.duyngo.topjob.exception.CheckInvalidException;
import com.duyngo.topjob.service.UserService;
import com.duyngo.topjob.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws CheckInvalidException {
        User currentUser = this.userService.getUserById(id);
        if (currentUser == null) {
            throw new CheckInvalidException("User with id " + id + " không tồn tại!");
        }
        return ResponseEntity.ok(this.userService.convertResUserDTO(currentUser));
    }

    @GetMapping()
    @ApiMessage("Get all user")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(spec, pageable));
    }

    @PostMapping()
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> create(@Valid @RequestBody User user) throws CheckInvalidException {
        User newUser = this.userService.createUSer(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResCreateUserDTO(newUser));
    }

    @PutMapping()
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUserDTO> update(@Valid @RequestBody ReqUserUpdateDTO requestUser)
            throws CheckInvalidException {
        User user = this.userService.updateUser(requestUser);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertResUpdateUserDTO(user));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws CheckInvalidException {
        this.userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }

}
