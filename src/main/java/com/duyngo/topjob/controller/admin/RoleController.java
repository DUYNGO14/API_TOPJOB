package com.duyngo.topjob.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Role;
import com.duyngo.topjob.domain.request.role_request.ReqRoleUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.role.ResRoleDTO;
import com.duyngo.topjob.exception.RoleException;
import com.duyngo.topjob.service.RoleService;
import com.duyngo.topjob.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

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
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") long id) throws RoleException {
        Role role = this.roleService.getRoleById(id);
        if (role == null) {
            throw new RoleException("Role with id = " + id + " not exist!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.convertResRoleDTO(role));
    }

    @GetMapping()
    @ApiMessage("Get all role")
    public ResponseEntity<ResultPaginationDTO> getAllRole(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.getAllRole(spec, pageable));
    }

    @PostMapping()
    @ApiMessage("Create new role")
    public ResponseEntity<ResRoleDTO> create(@Valid @RequestBody Role reqRole) throws RoleException {
        Role role = this.roleService.createRole(reqRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.convertResRoleDTO(role));
    }

    @PutMapping()
    @ApiMessage("Update role")
    public ResponseEntity<ResRoleDTO> update(@Valid @RequestBody Role reqRole) throws RoleException {
        Role role = this.roleService.updateRole(reqRole);
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.convertResRoleDTO(role));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete role by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws RoleException {
        this.roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
