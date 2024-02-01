package com.myshop.service;

import com.myshop.domain.User;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate redisTemplate;
    private final WebClient webClient;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        return UserDto.of(user);
    }

    @Transactional
    public UserDto updateUser(Long userId, UpdateUserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        user.updateUser(userDto);
        userRepository.save(user);
        return UserDto.of(user);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordDto passwordDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        user.updatePassword(passwordDto, bCryptPasswordEncoder);
        userRepository.save(user);
        String key = "JWT_TOKEN:" + user.getEmail();
        if (redisTemplate.opsForValue().get(key) != null) {
            redisTemplate.delete(key); // Token 삭제
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        // Notification 게시물 삭제 API 호출
        webClient.delete()
                .uri("http://localhost:8081/api/internal/newsfeeds/user/" + userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block(); // 여기서는 블로킹 호출을 사용
        // Post 모듈의 게시물 삭제 API 호출
        webClient.delete()
                .uri("http://localhost:8082/api/internal/posts/user/" + userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block(); // 여기서는 블로킹 호출을 사용

        userRepository.delete(user);
        String key = "JWT_TOKEN:" + user.getEmail();
        if (redisTemplate.opsForValue().get(key) != null) {
            redisTemplate.delete(key); // Token 삭제
        }
    }
}