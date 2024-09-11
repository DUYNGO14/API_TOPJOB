package com.duyngo.topjob.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Permission;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.PermissionException;
import com.duyngo.topjob.repository.PermissionRepository;
import com.duyngo.topjob.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission createPermission(Permission permission) throws PermissionException {
        if (this.isExistsPermission(permission)) {
            throw new PermissionException("Permission is exists!");
        }
        return this.permissionRepository.save(permission);
    }

    @Override
    public Permission updatePermission(Permission permission) throws PermissionException {
        Permission currentPermission = this.getPermissionById(permission.getId());
        if (currentPermission == null) {
            throw new PermissionException("Permission with id : " + permission.getId() + " not exists!");
        }

        if (this.isExistsPermission(permission)) {
            if (this.isExistsByName(permission.getName())) {
                throw new PermissionException("Permission is exists!");
            }
        }

        currentPermission.setName(permission.getName());
        currentPermission.setApiPath(permission.getApiPath());
        currentPermission.setModule(permission.getModule());
        currentPermission.setMethod(permission.getMethod());

        return this.permissionRepository.save(currentPermission);
    }

    @Override
    public void deletePermission(long id) throws PermissionException {
        // delete permission_role
        Permission currentPermission = this.getPermissionById(id);
        if (currentPermission == null) {
            throw new PermissionException("Permission with id : " + id + " not exists!");
        }
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    @Override
    public ResultPaginationDTO getAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(pagePermission.getTotalPages())
                .total(pagePermission.getTotalElements())
                .build();
        res = ResultPaginationDTO.builder()
                .meta(mt)
                .result(pagePermission.getContent())
                .build();
        return res;
    }

    @Override
    public Permission getPermissionById(long id) {
        Optional<Permission> p = this.permissionRepository.findById(id);
        if (p.isPresent()) {
            return p.get();
        }
        return null;
    }

    @Override
    public boolean isExistsByName(String name) {
        return this.permissionRepository.existsByName(name);
    }

    @Override
    public boolean isExistsPermission(Permission p) {
        return this.permissionRepository.existsByApiPathAndModuleAndMethod(p.getApiPath(), p.getModule(),
                p.getMethod());
    }

}
