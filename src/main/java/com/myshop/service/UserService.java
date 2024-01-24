package com.myshop.service;

import com.myshop.domain.User;
import com.myshop.dto.RegisterDto;
import com.myshop.dto.TokenDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.global.token.TokenManager;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public TokenResponseDto join(RegisterDto registerDto) {
        User user = userRepository.save(registerDto.toEntity());
        TokenDto tokenDto = TokenDto.builder().userId(user.getId()).build();
        return tokenManager.generateToken(tokenDto);
    }
}
