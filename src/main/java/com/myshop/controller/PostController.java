package com.myshop.controller;

import com.myshop.dto.CreatePostDto;
import com.myshop.dto.PostDetailDto;
import com.myshop.dto.PostDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public void createPost(@RequestBody CreatePostDto postDto) {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        postService.createPost(userId, postDto);
    }

    @GetMapping
    public List<PostDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{post_id}")
    public PostDetailDto getPostById(@PathVariable("post_id") Long postId) {
        return postService.getPostById(postId);
    }
}
