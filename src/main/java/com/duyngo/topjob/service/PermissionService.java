package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.Permission;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.exception.PermissionException;

public interface PermissionService {
    public Permission createPermission(Permission permission) throws PermissionException;

    public Permission updatePermission(Permission permission) throws PermissionException;

    public void deletePermission(long id) throws PermissionException;

    public ResultPaginationDTO getAllPermission(Specification<Permission> spec, Pageable pageable);

    public Permission getPermissionById(long id);

    public boolean isExistsByName(String name);

    public boolean isExistsPermission(Permission permission);
}
