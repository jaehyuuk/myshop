package com.myshop.restApiController;

import com.myshop.dto.PostResponseDto;
import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users")
    public List<PostResponseDto> getPostsByUserIds(@RequestParam List<Long> userIds) {
        return postService.getPostsByUserIds(userIds);
    }

}