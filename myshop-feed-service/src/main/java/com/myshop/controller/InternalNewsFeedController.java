package com.myshop.controller;

import com.myshop.global.dto.CreateNotificationDto;
import com.myshop.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/feeds")
@RequiredArgsConstructor
public class InternalNewsFeedController {
    private final NewsFeedService newsFeedService;

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteNewsfeedByUserId(@PathVariable Long userId) {
        newsFeedService.deleteNewsfeedByUserId(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notis")
    public ResponseEntity<?> createNotification(@RequestBody CreateNotificationDto request) {
        newsFeedService.createNotification(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notis/type/{typeId}")
    public ResponseEntity<?> deleteNotificationByTypeId(@PathVariable Long typeId) {
        newsFeedService.deleteNotificationByTypeId(typeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notis/post/{postId}")
    public ResponseEntity<?> deleteNotificationByPostId(@PathVariable Long postId) {
        newsFeedService.deleteNotificationByPostId(postId);
        return ResponseEntity.ok().build();
    }
}