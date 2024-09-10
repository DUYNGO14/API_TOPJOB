package com.duyngo.topjob.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.duyngo.topjob.domain.Role;
import com.duyngo.topjob.domain.request.role_request.ReqRoleUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.role.ResRoleDTO;
import com.duyngo.topjob.exception.RoleException;

public interface RoleService {
    public Role createRole(Role role) throws RoleException;

    public Role updateRole(ReqRoleUpdateDTO role) throws RoleException;

    public void deleteRole(long id) throws RoleException;

    public ResultPaginationDTO getAllRole(Specification<Role> spec, Pageable pageable);

    public Role getRoleById(long id);

    public ResRoleDTO convertResRoleDTO(Role role);

    public Role getRoleByName(String name);
}
