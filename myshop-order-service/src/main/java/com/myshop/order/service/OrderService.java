package com.myshop.order.service;

import com.myshop.global.exception.BadRequestException;
import com.myshop.domain.item.Item;
import com.myshop.domain.item.ReservedItem;
import com.myshop.item.repository.ItemRepository;
import com.myshop.domain.Order;
import com.myshop.domain.OrderItem;
import com.myshop.domain.OrderStatus;
import com.myshop.order.dto.CreateOrderItemDto;
import com.myshop.order.dto.OrderDto;
import com.myshop.order.repository.OrderRepository;
import com.myshop.user.domain.User;
import com.myshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Async
    public CompletableFuture<Long> prepareOrderAsync(Long userId, List<CreateOrderItemDto> orderItemDtos) {
        try {
            User user = findByUserId(userId);
            Order order = createOrder(user);
            addOrderItemsToOrder(order, orderItemDtos);
            Order savedOrder = orderRepository.save(order);
            return CompletableFuture.completedFuture(savedOrder.getId());
        } catch (Exception e) {
            log.error("Error processing order: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private Order createOrder(User user) {
        return Order.builder()
                .user(user)
                .status(OrderStatus.PREPARATION)
                .build();
    }

    private void addOrderItemsToOrder(Order order, List<CreateOrderItemDto> orderItemDtos) {
        for (CreateOrderItemDto dto : orderItemDtos) {
            Item item = findByItemId(dto.getItemId());
            if (item instanceof ReservedItem) {
                ReservedItem reservedItem = (ReservedItem) item;
                validateReservedItem(reservedItem);
            }
            OrderItem orderItem = dto.toEntity(item);
            order.addOrderItem(orderItem);
        }
    }

    private void validateReservedItem(ReservedItem reservedItem) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservedItem.getReservationStart()) || now.isAfter(reservedItem.getReservationEnd())) {
            throw new IllegalStateException("예약 가능한 시간이 아닙니다.");
        }
    }

    @Transactional
    @Async
    public CompletableFuture<OrderStatus> processOrderAsync(Long orderId) {
        try {
            Order order = findByOrderId(orderId);
            if (order.getStatus() == OrderStatus.ORDER) {
                log.info("already Order");
                return CompletableFuture.completedFuture(order.getStatus());
            }
            return orderPayAndUpdateStatus(order);
        } catch (Exception e) {
            log.error("Error processing order: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private CompletableFuture<OrderStatus> orderPayAndUpdateStatus(Order order) {
        boolean paymentSuccess = Math.random() < 0.8; // 결제 이탈율 20%

        if (!paymentSuccess) {
            order.cancel();
        } else {
            order.updateStatus(OrderStatus.ORDER);
        }
        orderRepository.save(order);
        return CompletableFuture.completedFuture(order.getStatus());
    }


    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = findByOrderId(orderId);
        validateUser(order, userId);
        order.cancel();
    }

    @Transactional
    public void removeOrderItem(Long userId, Long orderId, Long orderItemId) {
        Order order = findByOrderId(orderId);
        validateUser(order, userId);
        OrderItem orderItemToRemove = findOrderItemById(order, orderItemId);
        order.removeOrderItem(orderItemToRemove);
    }

    private OrderItem findOrderItemById(Order order, Long orderItemId) {
        return order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("주문 목록이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository.findAllWithItems();
        return orders.stream().map(OrderDto::of).collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findByOrderId(orderId);
        orderRepository.delete(order);
    }

    private User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
    }

    private Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestException("주문 내역이 존재하지 않습니다.")
        );
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestException("상품이 존재하지 않습니다.")
        );
    }

    private void validateUser(Order order, Long userId) {
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("주문 취소 권한이 없습니다.");
        }
    }
}
