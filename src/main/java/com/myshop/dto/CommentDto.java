package com.myshop.dto;

import com.myshop.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {
    private String content;
    private String name;
    private String profileImg;
    private Long userId;

    public static CommentDto getCommentDto(Comment comment) {
        return new CommentDto(
                comment.getContent(),
                comment.getWriter().getName(),
                comment.getWriter().getProfileImg(),
                comment.getWriter().getId());
    }
}
