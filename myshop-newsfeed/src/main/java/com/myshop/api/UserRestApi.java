package com.myshop.api;

import com.myshop.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/feeds")
@RequiredArgsConstructor
public class UserRestApi {
    private final NewsFeedService newsFeedService;

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteNewsfeedByUserId(@PathVariable Long userId) {
        newsFeedService.deleteNewsfeedByUserId(userId);
        return ResponseEntity.ok().build();
    }
}