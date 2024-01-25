package com.myshop.dto;

import com.myshop.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegisterDto {
    private String name;
    private String email;
    private String password;
    private String profileImg;
    private String introduce;

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .profileImg(profileImg)
                .introduce(introduce)
                .build();
    }
}