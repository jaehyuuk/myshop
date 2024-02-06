package com.myshop.api;

import com.myshop.global.dto.PostResponseDto;
import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/posts")
@RequiredArgsConstructor
public class NewsFeedRestApi {
    private final PostService postService;

    @PostMapping("/follows")
    public List<PostResponseDto> getPostsByUserIds(@RequestBody List<Long> followingIds) {
        return postService.getPostsByUserIds(followingIds);
    }
}