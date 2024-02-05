package com.myshop.controller;

import com.myshop.dto.FollowDto;
import com.myshop.dto.CreateNewsFeedDto;
import com.myshop.dto.NotificationDto;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class NewsFeedController {
    private final NewsFeedService newsFeedService;

    @GetMapping
    public List<CreateNewsFeedDto> getNewsFeed() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return newsFeedService.getNewsFeed(userId);
    }

    @GetMapping("/me")
    public List<NotificationDto> getMyNotis() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return newsFeedService.getMyNotis(userId);
    }

    @GetMapping("/follows")
    public List<FollowDto> getFollows() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return newsFeedService.getFollows(userId);
    }

    @PostMapping("/follows/{followingId}")
    public void follow(
            @PathVariable Long followingId
    ) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        newsFeedService.follow(userId, followingId);
    }
}
