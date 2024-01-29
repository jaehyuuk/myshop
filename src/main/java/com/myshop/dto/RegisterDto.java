package com.myshop.dto;

import com.myshop.domain.User;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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
}