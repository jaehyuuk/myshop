package com.myshop.order.dto;

import com.myshop.domain.item.Item;
import com.myshop.domain.OrderItem;
import lombok.Getter;

@Getter
public class CreateOrderItemDto {
    private Long itemId;
    private int count;

    public OrderItem toEntity(Item item) {
        return OrderItem.builder()
                .item(item)
                .orderPrice(item.getPrice() * count)
                .count(count)
                .build();
    }
}
