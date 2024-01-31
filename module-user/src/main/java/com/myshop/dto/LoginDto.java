package com.myshop.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class LoginDto {
    @Email
    private String email;
    private String password;

    @Builder
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
