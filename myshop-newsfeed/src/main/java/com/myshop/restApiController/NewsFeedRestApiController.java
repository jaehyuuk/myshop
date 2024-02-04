package com.myshop.restApiController;

import com.myshop.global.dto.CreateNotificationDto;
import com.myshop.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/feeds")
@RequiredArgsConstructor
public class NewsFeedRestApiController {
    private final NewsFeedService newsFeedService;

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteNewsfeedByUserId(@PathVariable Long userId) {
        // 사용자 ID를 기반으로 게시물 삭제
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