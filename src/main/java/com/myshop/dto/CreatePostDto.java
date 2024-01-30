package com.myshop.dto;

import com.myshop.domain.Post;
import com.myshop.domain.User;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class CreatePostDto {
    private String content;

    public Post toEntity(Long userId) {
        return Post.builder()
                .user(User.builder().id(userId).build())
                .content(content)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }
}
