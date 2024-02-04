package com.myshop.dto;

import com.myshop.domain.Post;
import com.myshop.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CreatePostDto {
    private String content;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .content(content)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }
}
