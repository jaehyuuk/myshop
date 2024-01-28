package com.myshop.controller;

import com.myshop.dto.FollowDto;
import com.myshop.dto.NewsFeedDto;
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
        Long followerId = AuthenticationUtils.getUserIdByToken();
        followService.follow(followerId, followingId);
    }

    @GetMapping
    public List<FollowDto> getFollows() {
        Long followerId = AuthenticationUtils.getUserIdByToken();
        return followService.getFollows(followerId);
    }

    @GetMapping("/feed")
    public List<NewsFeedDto> getfeeds() {
        Long followerId = AuthenticationUtils.getUserIdByToken();
        return followService.getFeeds(followerId);
    }

}
