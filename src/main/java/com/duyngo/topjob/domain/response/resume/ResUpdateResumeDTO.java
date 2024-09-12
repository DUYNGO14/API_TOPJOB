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
public class ResUpdateResumeDTO {
    private long id;
    private String email;
    private StatusEnum status;
    private String url;
    private Instant updatedAt;
    private String updatedBy;
}
