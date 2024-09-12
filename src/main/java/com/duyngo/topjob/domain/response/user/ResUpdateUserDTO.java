package com.duyngo.topjob.domain.response.user;

import java.time.Instant;

import com.duyngo.topjob.domain.response.user.ResUserDTO.CompanyUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResUpdateUserDTO {
    private long id;
    private String fullname;
    private String username;
    private String email;
    private String address;
    private String phonenumber;
    private Instant updateAt;
    private String updateBy;
    private RoleUser role;
    private CompanyUser company;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }

}
