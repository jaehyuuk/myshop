package com.myshop.order.dto;

import com.myshop.order.domain.Order;
import com.myshop.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class OrderDto {
    private Long id;
    private Long userId;
    private String name;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItems;
    private String createdAt;

    @Builder
    public OrderDto(Long id, Long userId, String name, OrderStatus orderStatus, List<OrderItemDto> orderItems, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.createdAt = createdAt;
    }

    public static OrderDto of (Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .name(order.getUser().getName())
                .orderStatus(order.getStatus())
                .orderItems(order.getOrderItems().stream().map(OrderItemDto::of).collect(toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }
}
