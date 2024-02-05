package com.myshop.api;

import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/posts")
@RequiredArgsConstructor
public class UserRestApi {
    private final PostService postService;

    @DeleteMapping("/user/{userId}")
    public void deletePostsByUserId(@PathVariable Long userId) {
        postService.deleteAllByUserId(userId);
    }
}
