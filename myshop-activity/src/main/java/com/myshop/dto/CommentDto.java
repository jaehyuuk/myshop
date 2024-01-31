package com.myshop.dto;

import com.myshop.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private LocalDateTime createdDate;

    @Builder
    public CommentDto(Long id, String content, String name, String profileImg, Long userId, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.createdDate = createdDate;
    }

    public static CommentDto of(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .name(comment.getWriter().getName())
                .profileImg(comment.getWriter().getProfileImg())
                .userId(comment.getWriter().getId())
                .createdDate(comment.getCreatedDate())
                .build();
    }
}
