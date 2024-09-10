package com.duyngo.topjob.domain.request.user_request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "Username không được để trống")
    private String email;
    @NotBlank(message = "Password không được để trống")
    private String password;

}
