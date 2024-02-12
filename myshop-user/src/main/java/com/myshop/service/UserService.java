package com.myshop.service;

import com.myshop.user.domain.User;
import com.myshop.global.exception.BadRequestException;
import com.myshop.user.repository.UserRepository;
import com.myshop.user.dto.UpdatePasswordDto;
import com.myshop.user.dto.UpdateUserDto;
import com.myshop.user.dto.UserDto;
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
        User user = findUserById(userId);
        return UserDto.of(user);
    }

    @Transactional
    public UserDto updateUser(Long userId, UpdateUserDto userDto) {
        User user = findUserById(userId);
        user.updateUser(userDto);
        userRepository.save(user);
        return UserDto.of(user);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordDto passwordDto) {
        User user = findUserById(userId);
        user.updatePassword(passwordDto, bCryptPasswordEncoder);
        userRepository.save(user);
        deleteToken(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        deleteNotification(userId);
        deletePost(userId);
        userRepository.delete(user);
        deleteToken(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
    }

    private void deleteToken(User user) {
        String key = "JWT_TOKEN:" + user.getEmail();
        if (redisTemplate.opsForValue().get(key) != null) {
            redisTemplate.delete(key); // Token 삭제
        }
    }

    // Rest Api
    private void deleteNotification(Long userId) {
        webClient.delete()
                .uri("http://localhost:8082/api/internal/feeds/user/" + userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void deletePost(Long userId) {
        webClient.delete()
                .uri("http://localhost:8083/api/internal/posts/user/" + userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}