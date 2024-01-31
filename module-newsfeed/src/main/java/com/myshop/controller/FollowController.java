package com.myshop.controller;

import com.myshop.dto.FollowDto;
import com.myshop.dto.NewsFeedDto;
import com.myshop.dto.UserDto;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{followingId}")
    public void follow(
            @PathVariable Long followingId
    ) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        followService.follow(userId, followingId);
    }

    @GetMapping
    public List<FollowDto> getFollows() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return followService.getFollows(userId);
    }

    @GetMapping("/feed")
    public List<NewsFeedDto> getfeeds() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return followService.getFeeds(userId);
    }

}
