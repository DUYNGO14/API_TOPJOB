package com.duyngo.topjob.domain.request.resume_request;

import com.duyngo.topjob.util.constant.StatusEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqResumeUpdateDTO {
    @NotBlank(message = "Id resume not blank")
    private long id;
    @NotBlank(message = "Status resume not blank")
    private StatusEnum status;
}
