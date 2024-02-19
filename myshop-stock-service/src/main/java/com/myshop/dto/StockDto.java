package com.myshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockDto {
    private Long itemId;
    private String name;
    private int stockQuantity;
}
