package com.duyngo.topjob.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duyngo.topjob.domain.Permission;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.PermissionException;
import com.duyngo.topjob.service.PermissionService;
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
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping()
    @ApiMessage("Get all permission")
    public ResponseEntity<ResultPaginationDTO> getAll(@Filter Specification<Permission> spec, Pageable pageable)
            throws PermissionException {
        return ResponseEntity.ok().body(this.permissionService.getAllPermission(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get permission by id")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") long id) throws PermissionException {
        Permission permission = this.permissionService.getPermissionById(id);
        if (permission == null) {
            throw new PermissionException("Permission with id : " + id + " not exists!");
        }
        return ResponseEntity.ok().body(permission);
    }

    @PostMapping()
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission requestPermission)
            throws PermissionException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.createPermission(requestPermission));
    }

    @PutMapping()
    @ApiMessage("Update permission ")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission requestPermission)
            throws PermissionException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.updatePermission(requestPermission));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws PermissionException {
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok().body(null);
    }
}
