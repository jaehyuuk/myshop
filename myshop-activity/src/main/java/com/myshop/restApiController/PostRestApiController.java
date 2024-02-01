package com.myshop.restApiController;

import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/posts")
@RequiredArgsConstructor
public class PostRestApiController {
    private final PostService postService;

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deletePostsByUserId(@PathVariable Long userId) {
        // 사용자 ID를 기반으로 게시물 삭제
        postService.deleteAllByUserId(userId);
        return ResponseEntity.ok().build();
    }
}