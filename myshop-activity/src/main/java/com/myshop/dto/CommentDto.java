package com.myshop.dto;

import com.myshop.domain.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private String createdAt;

    @Builder
    public CommentDto(Long id, String content, String name, String profileImg, Long userId, String createdAt) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static CommentDto of(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .name(comment.getWriter().getName())
                .profileImg(comment.getWriter().getProfileImg())
                .userId(comment.getWriter().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
