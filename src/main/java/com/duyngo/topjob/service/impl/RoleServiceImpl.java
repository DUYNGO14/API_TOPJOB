package com.duyngo.topjob.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyngo.topjob.domain.Permission;
import com.duyngo.topjob.domain.Role;
import com.duyngo.topjob.domain.request.role_request.ReqRoleUpdateDTO;
import com.duyngo.topjob.domain.response.ResultPaginationDTO;
import com.duyngo.topjob.domain.response.role.ResRoleDTO;
import com.duyngo.topjob.exception.RoleException;
import com.duyngo.topjob.repository.PermissionRepository;
import com.duyngo.topjob.repository.RoleRepository;
import com.duyngo.topjob.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role getRoleById(long id) {
        Optional<Role> role = this.roleRepository.findById(id);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Override
    public Role getRoleByName(String name) {
        Optional<Role> role = this.roleRepository.findByName(name);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Override
    public Role createRole(Role role) throws RoleException {
        boolean isExistsName = this.roleRepository.existsByName(role.getName());
        if (isExistsName) {
            throw new RoleException("Role with name :" + role.getName() + " is already in role");
        }
        if (role.getPermissions() != null) {
            List<Long> reqPermission = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermission = this.permissionRepository.findByIdIn(reqPermission);
            role.setPermissions(dbPermission);
        }
        return this.roleRepository.save(role);
    }

    @Override
    public Role updateRole(Role role) throws RoleException {
        Role currentRole = this.getRoleById(role.getId());
        if (currentRole == null) {
            throw new RoleException("Role with id = " + role.getId() + " not exist!");
        }
        if (!currentRole.getName().equals(role.getName())) {
            boolean isExistsName = this.getRoleByName(role.getName()) == null ? false : true;
            if (isExistsName) {
                throw new RoleException("Role with name :" + role.getName() + " is already in role");
            }
        }
        if (role.getPermissions() != null) {
            List<Long> reqPermission = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermission = this.permissionRepository.findByIdIn(reqPermission);
            currentRole.setPermissions(dbPermission);
        }
        currentRole.setName(role.getName());
        currentRole.setDescription(role.getDescription());
        currentRole.setActive(role.isActive());
        return this.roleRepository.save(currentRole);
    }

    @Override
    public void deleteRole(long id) throws RoleException {
        Role currentRole = this.getRoleById(id);
        if (currentRole == null) {
            throw new RoleException("Role with id = " + id + " not exist!");
        }
        // currentRole.setActive(false);
        // this.roleRepository.save(currentRole);
        this.roleRepository.deleteById(id);

    }

    @Override
    public ResultPaginationDTO getAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .pages(pageRole.getTotalPages())
                .total(pageRole.getTotalElements())
                .build();

        List<ResRoleDTO> lisRoleDTOs = pageRole.getContent()
                .stream()
                .map(item -> this.convertResRoleDTO(item))
                .collect(Collectors.toList());
        res = ResultPaginationDTO.builder()
                .meta(mt)
                .result(lisRoleDTOs)
                .build();
        return res;
    }

    @Override
    public ResRoleDTO convertResRoleDTO(Role role) {
        ResRoleDTO res = new ResRoleDTO();
        List<ResRoleDTO.PermissionRole> listPermissions = new ArrayList<ResRoleDTO.PermissionRole>();
        List<Permission> permissions = role.getPermissions();
        if (permissions.size() != 0) {
            for (Permission permission : permissions) {
                ResRoleDTO.PermissionRole pm = ResRoleDTO.PermissionRole.builder()
                        .apiPath(permission.getApiPath())
                        .name(permission.getName())
                        .method(permission.getMethod())
                        .module(permission.getModule())
                        .build();
                listPermissions.add(pm);
            }

        }
        res = ResRoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.isActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .permission(listPermissions)
                .build();
        return res;

    }

}
