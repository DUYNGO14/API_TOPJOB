package com.duyngo.topjob.domain.request.user_request;

import com.duyngo.topjob.domain.Company;
import com.duyngo.topjob.domain.Role;

import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqUserUpdateDTO {
    @NotNull(message = "id not null")
    private long id;
    private String fullname;
    private String username;
    // @Pattern(regexp = "0[3|5|7|8|9])+([0-9]{8}", message = "Enter a valid Mobile
    // Number")
    private String phonenumber;
    private String address;
    private Role role;
    private Company company;
}
