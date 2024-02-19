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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;

    @Transactional
    public Long prepareOrder(Long userId, List<CreateOrderItemDto> orderItemDtos) {
        User user = findByUserId(userId);
        Order order = createOrder(user);
        addOrderItemsToOrder(order, orderItemDtos);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
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
    public OrderStatus processOrder(Long userId, Long orderId) {
        findByUserId(userId);
        Order order = findByOrderId(orderId);
        if (order.getStatus() == OrderStatus.ORDER) {
            throw new BadRequestException("이미 주문이 완료 되었습니다.");
        }
        return orderPayAndUpdateStatus(order);
    }

    private OrderStatus orderPayAndUpdateStatus(Order order) {
        boolean paymentSuccess = Math.random() < 0.8; // 결제 이탈율 20%
        if (!paymentSuccess) {
            cancel(order);
        } else {
            order.updateStatus(OrderStatus.ORDER);
            // 결제 성공 시 각 주문 항목에 대해 재고 수량 업데이트 (redis)
            order.getOrderItems().forEach(orderItem -> {
                updateStockQuantity(orderItem.getItem().getId(), orderItem.getItem().getStockQuantity() - orderItem.getCount())
                        .subscribe(result -> log.info(result),
                                error -> log.error("Error updating stock: ", error));
            });
        }
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getStatus();
    }

    private void cancel(Order order) {
        order.updateStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.getItem().addStock(orderItem.getCount());
            // redis
            int restoredQuantity = orderItem.getItem().getStockQuantity() + orderItem.getCount();
            updateStockQuantity(orderItem.getItem().getId(), restoredQuantity)
                    .subscribe(result -> log.info(result),
                            error -> log.error("Error restoring stock: ", error));
        }
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
        cancel(order);
    }

    @Transactional
    public void removeOrderItem(Long userId, Long orderId, Long orderItemId) {
        Order order = findByOrderId(orderId);
        validateUser(order, userId);
        OrderItem orderItemToRemove = findOrderItemById(order, orderItemId);
        // redis
        int restoredQuantity = orderItemToRemove.getItem().getStockQuantity() + orderItemToRemove.getCount();
        updateStockQuantity(orderItemToRemove.getItem().getId(), restoredQuantity)
                .subscribe(result -> log.info(result),
                        error -> log.error("Error restoring stock after item removal: ", error));
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
        // 주문에 포함된 각 주문 항목에 대해 재고 복원
        order.getOrderItems().forEach(orderItem -> {
            int restoredQuantity = orderItem.getItem().getStockQuantity() + orderItem.getCount();
            updateStockQuantity(orderItem.getItem().getId(), restoredQuantity)
                    .subscribe(
                            result -> log.info(result),
                            error -> log.error("Error restoring stock before deleting order: ", error)
                    );
        });
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

    public Mono<String> updateStockQuantity(Long itemId, int stockQuantity) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8085)
                        .path("/api/internal/stocks/{itemId}")
                        .queryParam("stockQuantity", stockQuantity)
                        .build(itemId))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error updating stock quantity: " + e.getMessage()));
    }
}
