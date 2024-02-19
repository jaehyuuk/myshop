package com.myshop.controller;

import com.myshop.domain.OrderStatus;
import com.myshop.dto.PrepareOrderRequest;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.dto.CreateOrderItemDto;
import com.myshop.dto.OrderDto;
import com.myshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> prepareOrder(@RequestBody PrepareOrderRequest request) {
        try {
            Long orderId = orderService.prepareOrder(request.getUserId(), request.getOrderItemDtos());
            return ResponseEntity.ok().body("Order prepared successfully with ID: " + orderId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error preparing order: " + e.getMessage());
        }
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<?> processOrder(@PathVariable Long orderId) {
        try {
            OrderStatus status = orderService.processOrder(orderId);
            return ResponseEntity.ok().body("Order processed with status: " + status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing order: " + e.getMessage());
        }
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
