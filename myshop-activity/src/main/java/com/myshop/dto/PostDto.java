package com.myshop.dto;

import com.myshop.domain.Post;
import lombok.Builder;
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
    private String createdAt;

    @Builder
    public PostDto(Long id, String content, String name, String profileImg, Long userId, Integer likeCount, Integer commentCount, String createdAt) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
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
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Override
    public String toString() {
        return "PostDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", userId=" + userId +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                '}';
    }
}
