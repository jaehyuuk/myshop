package com.myshop.dto;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class LoginDto {
    @Email
    private String email;
    private String password;
}
