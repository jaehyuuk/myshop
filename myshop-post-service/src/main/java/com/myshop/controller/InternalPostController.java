package com.myshop.controller;

import com.myshop.global.dto.PostResponseDto;
import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/posts")
@RequiredArgsConstructor
public class InternalPostController {
    private final PostService postService;

    @DeleteMapping("/user/{userId}")
    public void deletePostsByUserId(@PathVariable Long userId) {
        postService.deleteAllByUserId(userId);
    }

    @PostMapping("/follows")
    public List<PostResponseDto> getPostsByUserIds(@RequestBody List<Long> followingIds) {
        return postService.getPostsByUserIds(followingIds);
    }
}
