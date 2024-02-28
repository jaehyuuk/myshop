package com.myshop.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStockDto {
    private Long stockId;
    private Long orderId;
    private Long itemId;
    private Long userId;
    private int stockQuantity;
}
