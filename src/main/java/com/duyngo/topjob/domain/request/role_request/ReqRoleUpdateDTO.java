package com.duyngo.topjob.domain.request.role_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.duyngo.topjob.domain.Permission;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqRoleUpdateDTO {
    @NotNull(message = "Id role not null!")
    private long id;
    @NotNull(message = "Name role not null!")
    @NotBlank(message = "Name role not blank!")
    private String name;
    private String description;
    private boolean active;
    private List<Permission> permissions;
}
