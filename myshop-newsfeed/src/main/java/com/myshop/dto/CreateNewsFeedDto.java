package com.myshop.dto;

import com.myshop.global.dto.PostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateNewsFeedDto {
    private List<NotificationDto> notificationDtos;
    private List<PostResponseDto> postDtos;
}
