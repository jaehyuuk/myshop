package com.myshop.item.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateItemDto {
    private String name;
    private String content;
    private Integer price;
    private Integer stockQuantity;
    // ReservedItem 업데이트를 위한 필드
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
}
