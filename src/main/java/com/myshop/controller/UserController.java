package com.myshop.controller;

import com.myshop.domain.User;
import com.myshop.dto.UpdateUserDto;
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
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public void updateProfile(@RequestBody UpdateUserDto userDto) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        userService.updateUser(userId, userDto);
    }
}
