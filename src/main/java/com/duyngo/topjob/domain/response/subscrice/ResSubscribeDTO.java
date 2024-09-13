package com.duyngo.topjob.domain.response.subscrice;

import java.util.List;

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
public class ResSubscribeDTO {
    private long id;
    private UserSub user;
    private List<SkillSub> skill;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserSub {
        private long id;
        private String email;
        private String fullname;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillSub {
        private long id;
        private String name;
    }

}
