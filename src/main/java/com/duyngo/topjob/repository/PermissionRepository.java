package com.duyngo.topjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.duyngo.topjob.domain.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsByApiPathAndModuleAndMethod(String apiPath, String module, String method);

    boolean existsByName(String name);

    Optional<Permission> findById(long id);

    List<Permission> findByIdIn(List<Long> reqPermission);
}
