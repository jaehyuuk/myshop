package com.myshop.dto;

import com.myshop.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdDate;

    @Builder
    public PostDto(Long id, String content, String name, String profileImg, Long userId, Integer likeCount, Integer commentCount, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdDate = createdDate;
    }

    public static PostDto of(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .name(post.getUser().getName())
                .profileImg(post.getUser().getProfileImg())
                .userId(post.getUser().getId())
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .createdDate(post.getCreatedDate())
                .build();
    }
}
