package com.myshop.order.dto;

import com.myshop.item.domain.Item;
import com.myshop.order.domain.OrderItem;
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
