package com.myshop.order.dto;

import com.myshop.domain.item.GeneralItem;
import com.myshop.domain.item.ReservedItem;
import com.myshop.domain.OrderItem;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemDto {
    private String itemName;
    private String itemType;
    private int orderPrice;
    private int count;

    @Builder
    public OrderItemDto(String itemName, String itemType, int orderPrice, int count) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    public static OrderItemDto of (OrderItem orderItem) {
        // Item의 실제 타입에 따라 itemType을 설정
        String type = orderItem.getItem() instanceof GeneralItem ? "GENERAL" :
                orderItem.getItem() instanceof ReservedItem ? "RESERVED" : "UNKNOWN";

        return OrderItemDto.builder()
                .itemName(orderItem.getItem().getName())
                .itemType(type)
                .orderPrice(orderItem.getOrderPrice())
                .count(orderItem.getCount())
                .build();
    }
}
