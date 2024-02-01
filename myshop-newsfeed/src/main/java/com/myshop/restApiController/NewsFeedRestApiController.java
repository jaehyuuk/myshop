package com.myshop.restApiController;

import com.myshop.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}