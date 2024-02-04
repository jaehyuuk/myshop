package com.myshop.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserDto {
    private String name;
    private String profileImg;
    private String introduce;

    @Builder
    public UpdateUserDto(String name, String profileImg, String introduce) {
        this.name = name;
        this.profileImg = profileImg;
        this.introduce = introduce;
    }

}
