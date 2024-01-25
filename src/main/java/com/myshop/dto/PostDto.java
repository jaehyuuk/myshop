package com.myshop.dto;

import com.myshop.domain.Post;
import lombok.Getter;

@Getter
public class PostDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private Integer likeCount;
    private Integer commentCount;

    public PostDto(Post post) {
        id = post.getId();
        content = post.getContent();
        name = post.getUser().getName();
        profileImg = post.getUser().getProfileImg();
        userId = post.getUser().getId();
        likeCount = post.getLikes().size();
        commentCount = post.getComments().size();
    }
}
