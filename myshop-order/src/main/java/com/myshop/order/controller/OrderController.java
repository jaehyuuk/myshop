package com.myshop.order.controller;

import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.order.dto.CreateOrderItemDto;
import com.myshop.order.dto.OrderDto;
import com.myshop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Long createOrder(@RequestBody List<CreateOrderItemDto> dtos) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return orderService.createOrder(userId, dtos);
    }

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

}
