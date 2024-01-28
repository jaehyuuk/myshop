package com.myshop.controller;

import com.myshop.dto.LoginDto;
import com.myshop.dto.RegisterDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.dto.UserDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/join")
    public TokenResponseDto join(@RequestBody RegisterDto registerDto) {
        return authService.join(registerDto);
    }

    @GetMapping
    public UserDto getAuth() {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        return authService.getAuth(userId);
    }

    @PostMapping
    public TokenResponseDto login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

}