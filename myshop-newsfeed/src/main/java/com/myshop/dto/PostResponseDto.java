package com.myshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("content")
    private String content;
    @JsonProperty("name")
    private String name;
    @JsonProperty("profileImg")
    private String profileImg;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("likeCount")
    private Integer likeCount;
    @JsonProperty("commentCount")
    private Integer commentCount;
    @JsonProperty("createdAt")
    private String createdAt;

    @Builder
    public PostResponseDto(Long id, String content, String name, String profileImg, Long userId, Integer likeCount, Integer commentCount, String createdAt) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PostResponseDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", userId=" + userId +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
