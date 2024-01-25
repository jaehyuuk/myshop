package com.myshop.controller;

import com.myshop.domain.User;
import com.myshop.dto.UpdatePasswordDto;
import com.myshop.dto.UpdateUserDto;
import com.myshop.dto.UserDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public void updateUser(@RequestBody UpdateUserDto userDto) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        userService.updateUser(userId, userDto);
    }

    @PostMapping("/update")
    public void updatePassword(@RequestBody UpdatePasswordDto passwordDto) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        userService.updatePassword(userId, passwordDto);
    }

}
