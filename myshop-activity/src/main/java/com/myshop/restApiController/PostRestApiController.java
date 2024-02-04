package com.myshop.restApiController;

import com.myshop.dto.PostDto;
import com.myshop.global.dto.PostResponseDto;
import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/internal/posts")
@RequiredArgsConstructor
public class PostRestApiController {
    private final PostService postService;

    @DeleteMapping("/user/{userId}")
    public void deletePostsByUserId(@PathVariable Long userId) {
        postService.deleteAllByUserId(userId);
    }

    @PostMapping("/follows")
    public Flux<PostResponseDto> getPostsByUserIds(@RequestBody List<Long> followingIds) {
        return postService.getPostsByUserIds(followingIds);
    }
}