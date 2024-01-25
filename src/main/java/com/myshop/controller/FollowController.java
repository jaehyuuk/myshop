package com.myshop.controller;

import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/{followingId}")
    public ResponseEntity<?> follow(@PathVariable Long followingId) {
        TokenContext context = TokenContextHolder.getContext();
        Long followerId = context.getUserId();
        followService.follow(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{followingId}")
    public ResponseEntity<?> unfollow(@PathVariable Long followingId) {
        TokenContext context = TokenContextHolder.getContext();
        Long followerId = context.getUserId();
        followService.unfollow(followerId, followingId);
        return ResponseEntity.ok().build();
    }
}
