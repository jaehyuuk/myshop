package com.myshop.dto;

import com.myshop.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.myshop.domain.NotiType.*;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private String message;
    private LocalDateTime createdAt;

    public static NotificationDto getNotification(Notification notification) {
        String message = "";
        message += notification.getFromUser().getName();
        message += "님이 ";
        message += notification.getToUser().getName();
        if (notification.getType().equals(COMMENT)) message += "님의 포스트에 댓글을 남겼습니다.";
        if (notification.getType().equals(LIKE)) message += "님의 포스트를 좋아합니다.";
        if (notification.getType().equals(FOLLOW)) message += "님을 팔로우 합니다.";
        return new NotificationDto(message, notification.getCreatedAt());
    }

    public static NotificationDto getMyNotification(Notification notification) {
        String message = "";
        message += notification.getFromUser().getName();
        message += "님이 ";
        if (notification.getType().equals(COMMENT)) message += "포스트에 댓글을 남겼습니다.";
        if (notification.getType().equals(LIKE)) message += "포스트를 좋아합니다.";
        if (notification.getType().equals(FOLLOW)) message += "당신을 팔로우 합니다.";
        return new NotificationDto(message, notification.getCreatedAt());
    }
}
