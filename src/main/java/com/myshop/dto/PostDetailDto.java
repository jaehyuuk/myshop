package com.myshop.dto;

import com.myshop.domain.Post;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostDetailDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private List<CommentDto> comments;

    public PostDetailDto(Post post) {
        id = post.getId();
        content = post.getContent();
        name = post.getUser().getName();
        profileImg = post.getUser().getProfileImg();
        userId = post.getUser().getId();
        comments = post.getComments().stream().map(CommentDto::getCommentDto).collect(Collectors.toList());
    }
}
