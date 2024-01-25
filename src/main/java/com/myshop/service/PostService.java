package com.myshop.service;

import com.myshop.domain.Post;
import com.myshop.dto.CreatePostDto;
import com.myshop.dto.PostDetailDto;
import com.myshop.dto.PostDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void createPost(Long userId, CreatePostDto postDto) {
        postRepository.save(postDto.toEntity(userId));
    }

    public List<PostDto> getPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostDto::new).collect(Collectors.toList());
    }

    public PostDetailDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("포스트가 없습니다.")
        );
        return new PostDetailDto(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
