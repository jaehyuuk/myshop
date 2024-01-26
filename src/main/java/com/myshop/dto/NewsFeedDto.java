package com.myshop.dto;

import com.myshop.domain.Notification;
import com.myshop.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class NewsFeedDto {
    private List<NotificationDto> notificationDtos;
    private List<PostDto> postDtos;

    public static NewsFeedDto getNewsfeedDto(List<Notification> notifications, List<Post> posts) {
        return new NewsFeedDto(
                notifications.stream().map(NotificationDto::getNotification).collect(Collectors.toList()),
                posts.stream().map(PostDto::getPostDto).collect(Collectors.toList())
        );
    }
}
