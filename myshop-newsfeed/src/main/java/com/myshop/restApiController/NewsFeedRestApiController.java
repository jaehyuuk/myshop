package com.myshop.restApiController;

import com.myshop.dto.NotificationCreateRequest;
import com.myshop.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/newsfeeds")
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
    public ResponseEntity<?> createNotification(@RequestBody NotificationCreateRequest request) {
        newsFeedService.createNotification(request);
        return ResponseEntity.ok().build();
    }
}