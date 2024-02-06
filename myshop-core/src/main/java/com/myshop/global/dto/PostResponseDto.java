package com.myshop.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private String name;
    private String profileImg;
    private Long userId;
    private Integer likeCount;
    private Integer commentCount;
    private String createdAt;
}
