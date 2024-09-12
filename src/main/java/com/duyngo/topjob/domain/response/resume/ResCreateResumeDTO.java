package com.duyngo.topjob.domain.response.resume;

import java.time.Instant;

import com.duyngo.topjob.util.constant.StatusEnum;

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
public class ResCreateResumeDTO {
    private long id;
    private String email;
    private String url;
    private StatusEnum status;
    private Instant createdAt;
    private String createdBy;
}
