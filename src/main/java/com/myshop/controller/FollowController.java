package com.myshop.controller;

import com.myshop.dto.FollowDto;
import com.myshop.dto.PostDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/{followingId}")
    public void follow(@PathVariable Long followingId) {
        TokenContext context = TokenContextHolder.getContext();
        Long followerId = context.getUserId();
        followService.follow(followerId, followingId);
    }

    @DeleteMapping("/unfollow/{followingId}")
    public void unfollow(@PathVariable Long followingId) {
        TokenContext context = TokenContextHolder.getContext();
        Long followerId = context.getUserId();
        followService.unfollow(followerId, followingId);
    }

    @GetMapping("/follow")
    public List<FollowDto> getFollows() {
        TokenContext context = TokenContextHolder.getContext();
        Long followerId = context.getUserId();
        return followService.getFollows(followerId);
    }

    @GetMapping
    public List<PostDto> getfeeds() {
        TokenContext context = TokenContextHolder.getContext();
        Long followerId = context.getUserId();
        return followService.getFeeds(followerId);
    }

}
