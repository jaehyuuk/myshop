package com.myshop.dto;

import com.myshop.domain.User;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class RegisterDto {
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank
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

    @Builder
    public RegisterDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}