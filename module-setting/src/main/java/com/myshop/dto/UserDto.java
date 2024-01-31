package com.myshop.dto;

import com.myshop.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String profileImg;
    private String introduce;

    @Builder
    public UserDto(Long id, String name, String email, String profileImg, String introduce) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;
        this.introduce = introduce;
    }

    public static UserDto of (User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .introduce(user.getIntroduce())
                .build();
    }

}
