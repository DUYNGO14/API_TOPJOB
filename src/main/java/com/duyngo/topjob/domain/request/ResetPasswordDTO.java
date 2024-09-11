package com.duyngo.topjob.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordDTO {
    @NotBlank(message = "SecretKey must be not blank")
    private String secretKey;

    @NotBlank(message = "Password must be not blank")
    private String password;

    @NotBlank(message = "ConfirmPassword must be not blank")
    private String confirmPassword;
}
