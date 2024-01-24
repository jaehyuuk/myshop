package com.myshop.controller;

import com.myshop.dto.RegisterDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/join")
    public TokenResponseDto join(@RequestBody RegisterDto registerDto) {
        return userService.join(registerDto);
    }
}