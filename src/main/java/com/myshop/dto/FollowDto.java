package com.myshop.dto;

import com.myshop.domain.Follow;
import lombok.Getter;

@Getter
public class FollowDto {
    private Long id;
    private String name;
    private String profileImg;
    private String introduce;

    public FollowDto(Follow follow) {
        id = follow.getFollowing().getId();
        name = follow.getFollowing().getName();
        profileImg = follow.getFollowing().getProfileImg();
        introduce = follow.getFollowing().getIntroduce();
    }
}
