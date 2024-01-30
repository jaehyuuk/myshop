package com.myshop.controller;

import com.myshop.dto.LoginDto;
import com.myshop.dto.RegisterDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.dto.UserDto;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/join")
    public void join(
            @Valid @RequestBody RegisterDto registerDto
    ) {
        authService.join(registerDto);
    }

    @PostMapping("/login")
    public TokenResponseDto login(
            @Valid @RequestBody LoginDto loginDto
    ) {
        return authService.login(loginDto);
    }

    @PostMapping("/logout")
    public void logout() {
        authService.logout(AuthenticationUtils.getUserIdByToken());
    }

}