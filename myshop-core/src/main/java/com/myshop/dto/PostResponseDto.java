package com.myshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdDate;

    @Builder
    public PostResponseDto(Long id, String content, String name, String profileImg, Long userId, Integer likeCount, Integer commentCount, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.profileImg = profileImg;
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdDate = createdDate;
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
                ", createdDate=" + createdDate +
                '}';
    }
}
