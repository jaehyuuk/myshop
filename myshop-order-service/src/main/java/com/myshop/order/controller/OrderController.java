package com.myshop.order.controller;

import com.myshop.domain.OrderStatus;
import com.myshop.order.dto.CreateOrderItemDto;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.order.dto.OrderDto;
import com.myshop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> prepareOrder(@RequestBody List<CreateOrderItemDto> orderItemDtos) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        Long orderId = orderService.prepareOrder(userId, orderItemDtos);
        return ResponseEntity.ok().body(orderId);
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<OrderStatus> processOrder(@PathVariable Long orderId) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        OrderStatus status = orderService.processOrder(userId, orderId);
        return ResponseEntity.ok().body(status);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDto>> getUserOrders() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        List<OrderDto> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<?> removeOrderItem(@PathVariable Long orderId, @PathVariable Long orderItemId) {
        Long userId = AuthenticationUtils.getUserIdByToken();
        orderService.removeOrderItem(userId, orderId, orderItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders() {
        List<OrderDto> orders = orderService.getOrders();
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
