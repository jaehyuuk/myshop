package com.myshop.service;

import com.myshop.domain.User;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.global.token.TokenManager;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final TokenManager tokenManager;

    @Transactional
    public TokenResponseDto join(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("이미 가입되어있는 이메일 입니다.");
        }
        User user = registerDto.toEntity();
        user.hashPassword(bCryptPasswordEncoder);
        userRepository.save(user);
        TokenDto tokenDto = TokenDto.builder().userId(user.getId()).build();
        return tokenManager.generateToken(tokenDto);
    }

    @Transactional
    public UserDto getAuth(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("로그인이 필요합니다.")
        );
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .introduce(user.getIntroduce())
                .build();
    }

    @Transactional
    public TokenResponseDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new BadRequestException("이메일이 존재하지 않습니다.")
        );
        user.checkPassword(loginDto.getPassword(), bCryptPasswordEncoder);
        TokenDto tokenDto = TokenDto.builder().userId(user.getId()).build();
        return tokenManager.generateToken(tokenDto);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            userDtos.add(UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .profileImg(user.getProfileImg())
                    .introduce(user.getIntroduce())
                    .build());
        }
        return userDtos;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 회원입니다.")
        );
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .introduce(user.getIntroduce())
                .build();
        return userDto;
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("회원가입을 해주세요.")
        );
        user.update(userDto);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordDto passwordDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("회원가입을 해주세요.")
        );
        user.updatePassword(passwordDto);
        user.hashPassword(bCryptPasswordEncoder);
        userRepository.save(user);
    }
}
