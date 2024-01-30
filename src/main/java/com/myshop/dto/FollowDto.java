package com.myshop.dto;

import com.myshop.domain.Follow;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FollowDto {
    private Long id;
    private String name;
    private String profileImg;
    private String introduce;

    @Builder
    public FollowDto(Long id, String name, String profileImg, String introduce) {
        this.id = id;
        this.name = name;
        this.profileImg = profileImg;
        this.introduce = introduce;
    }

    public static FollowDto of (Follow follow) {
        return FollowDto.builder()
                .id(follow.getFollowing().getId())
                .name(follow.getFollowing().getName())
                .profileImg(follow.getFollowing().getProfileImg())
                .introduce(follow.getFollowing().getIntroduce())
                .build();
    }
}
