package com.myshop.dto;

import com.myshop.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private LocalDateTime createdDate;

    @Builder
    public PostDetailDto(Long id, String content, String name, String profileImg, Long userId, List<CommentDto> comments, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.comments = comments;
        this.createdDate = createdDate;
    }

    public static PostDetailDto of(Post post) {
        return PostDetailDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .name(post.getUser().getName())
                .profileImg(post.getUser().getProfileImg())
                .userId(post.getUser().getId())
                .comments(post.getComments().stream().map(CommentDto::of).collect(Collectors.toList()))
                .createdDate(post.getCreatedDate())
                .build();
    }
}
