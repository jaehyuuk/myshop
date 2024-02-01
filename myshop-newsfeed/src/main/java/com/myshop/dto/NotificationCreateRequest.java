package com.myshop.dto;

import lombok.Getter;

@Getter
public class NotificationCreateRequest {
    private Long fromUserId;
    private Long toUserId;
    private String type;
    private Long postId;
    private Long typeId;
}
