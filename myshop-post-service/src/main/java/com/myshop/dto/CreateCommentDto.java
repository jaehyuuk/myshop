package com.myshop.dto;

import com.myshop.domain.Comment;
import com.myshop.user.domain.User;
import lombok.Getter;

@Getter
public class CreateCommentDto {
    private String content;

    public Comment toEntity(User writer) {
        return Comment.builder()
                .writer(writer)
                .content(content)
                .build();
    }
}
