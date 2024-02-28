package com.myshop.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStockDto {
    private Long orderId;
    private Long userId;
    private int stockQuantity;
}
