package com.myshop.dto;

import com.myshop.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private String message;
    private String createdAt;

    public static NotificationDto getFollowNotification(Notification notification) {
        String fromUserName = notification.getFromUser().getName();
        String toUserName = notification.getToUser().getName();
        Long postId = notification.getPostId();
        String formattedMessage = notification.getType().formatMessage(fromUserName, toUserName, postId, false);

        return new NotificationDto(formattedMessage, notification.getCreatedAt());
    }

    public static NotificationDto getMyNotification(Notification notification) {
        String fromUserName = notification.getFromUser().getName();
        Long postId = notification.getPostId();
        String formattedMessage = notification.getType().formatMessage(fromUserName, "", postId, true);

        return new NotificationDto(formattedMessage, notification.getCreatedAt());
    }
}
