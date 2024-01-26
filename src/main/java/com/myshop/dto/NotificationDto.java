package com.myshop.dto;

import com.myshop.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private String message;

    public static NotificationDto getNotification(Notification notification) {
        return new NotificationDto(notification.getMessage());
    }
}
