package com.duyngo.topjob.domain.response.user;

import java.time.Instant;

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
public class ResUserDTO {
    private long id;
    private String fullname;
    private String username;
    private String email;
    private String address;
    private String phonenumber;
    private boolean deleted;
    private Instant createAt;
    private String createBy;
    private Instant updateAt;
    private String updateBy;
    private RoleUser role;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
}
