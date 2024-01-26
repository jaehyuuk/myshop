package com.myshop.dto;

import com.myshop.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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

    public static PostDto getPostDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getContent(),
                post.getUser().getName(),
                post.getUser().getProfileImg(),
                post.getUser().getId(),
                post.getLikes().size(),
                post.getComments().size());
    }
}
