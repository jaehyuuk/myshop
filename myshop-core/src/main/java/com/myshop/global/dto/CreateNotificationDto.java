package com.myshop.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNotificationDto {
    private Long fromUserId;
    private Long toUserId;
    private String type;
    private Long postId;
    private Long typeId;
}
