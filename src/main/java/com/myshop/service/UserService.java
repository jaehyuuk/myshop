package com.myshop.service;

import com.myshop.domain.User;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

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
        user.updateUser(userDto);
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

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("회원가입을 해주세요,")
        );
        postRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }
}
