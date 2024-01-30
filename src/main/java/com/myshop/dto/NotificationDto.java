package com.myshop.dto;

import com.myshop.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private String message;
    private LocalDateTime createdAt;

    public static NotificationDto getFollowNotification(Notification notification) {
        String fromUserName = notification.getFromUser().getName();
        String toUserName = notification.getToUser().getName();
        String formattedMessage = notification.getType().formatMessage(fromUserName, toUserName, false);

        return new NotificationDto(formattedMessage, notification.getCreatedAt());
    }

    public static NotificationDto getMyNotification(Notification notification) {
        String fromUserName = notification.getFromUser().getName();
        String formattedMessage = notification.getType().formatMessage(fromUserName, "", true);

        return new NotificationDto(formattedMessage, notification.getCreatedAt());
    }
}
