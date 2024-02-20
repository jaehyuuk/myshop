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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
        User user = findEntityById(userRepository::findById, userId, "회원");
        Order order = createAndSaveOrder(user, orderItemDtos);
        return order.getId();
    }

    private Order createAndSaveOrder(User user, List<CreateOrderItemDto> orderItemDtos) {
        Order order = new Order(user, OrderStatus.PREPARATION);
        orderItemDtos.forEach(dto -> processOrderItem(dto, order));
        return orderRepository.save(order);
    }

    private void processOrderItem(CreateOrderItemDto dto, Order order) {
        Item item = findEntityById(itemRepository::findById, dto.getItemId(), "상품");
        validateItemForOrder(item);
        OrderItem orderItem = dto.toEntity(item);
        order.addOrderItem(orderItem);
    }

    private void validateItemForOrder(Item item) {
        if (item instanceof ReservedItem) {
            ReservedItem reservedItem = (ReservedItem) item;
            validateReservedItem(reservedItem);
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
        findEntityById(userRepository::findById, userId, "회원");
        Order order = findEntityById(orderRepository::findById, orderId, "주문");
        if (order.getStatus() == OrderStatus.ORDER) {
            throw new BadRequestException("주문 상태가 준비 중이 아니어서 변경할 수 없습니다.");
        }
        return orderPayAndUpdateStatus(order);
    }

    private OrderStatus orderPayAndUpdateStatus(Order order) {
        // 결제 이탈율 20%
        if (Math.random() < 0.2) {
            order.updateStatus(OrderStatus.CANCEL);
            order.cancel();
            return order.getStatus();
        }

        // 결제 실패율 20%
        if (Math.random() < 0.2) {
            order.updateStatus(OrderStatus.FAIL);
            order.cancel();
            return order.getStatus();
        }

        // 결제 성공
        order.updateStatus(OrderStatus.ORDER);
        updateOrderItemsStock(order);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getStatus();
    }

    private void updateOrderItemsStock(Order order) {
        // 결제 성공 시 각 주문 항목에 대해 재고 수량 업데이트
        order.getOrderItems().forEach(orderItem -> {
            int newStockQuantity = orderItem.getItem().getStockQuantity();
            updateStockQuantity(orderItem.getItem().getId(), newStockQuantity)
                    .subscribe(result -> log.info(result),
                            error -> log.error("Error updating stock: ", error));
        });
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = findEntityById(orderRepository::findById, orderId, "주문");
        validateUser(order, userId);
        if (order.getStatus() != OrderStatus.PREPARATION) {
            throw new BadRequestException("주문 상태가 준비 중이 아니어서 변경할 수 없습니다.");
        }
        order.updateStatus(OrderStatus.CANCEL);
        order.cancel();
        orderRepository.save(order);
    }

    @Transactional
    public void removeOrderItem(Long userId, Long orderId, Long orderItemId) {
        Order order = findEntityById(orderRepository::findById, orderId, "주문");
        validateUser(order, userId);
        if (order.getStatus() != OrderStatus.PREPARATION) {
            throw new BadRequestException("주문 상태가 준비 중이 아니어서 변경할 수 없습니다.");
        }
        OrderItem orderItem = findOrderItemById(order, orderItemId);
        order.removeOrderItem(orderItem);
        orderRepository.save(order);
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

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findEntityById(orderRepository::findById, orderId, "주문");
        // 주문 삭제 시 재고 복원 로직
        order.getOrderItems().forEach(orderItem -> {
            int restoredQuantity = orderItem.getItem().getStockQuantity() + orderItem.getCount();
            updateStockQuantity(orderItem.getItem().getId(), restoredQuantity)
                    .subscribe(result -> log.info(result),
                            error -> log.error("Error restoring stock: ", error));
        });
        orderRepository.delete(order);
    }

    private <T> T findEntityById(Function<Long, Optional<T>> finder, Long id, String entityName) {
        return finder.apply(id).orElseThrow(() -> new BadRequestException(entityName + " 정보를 찾을 수 없습니다."));
    }

    private void validateUser(Order order, Long userId) {
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("주문 취소 권한이 없습니다.");
        }
    }

    public Mono<String> updateStockQuantity(Long itemId, int stockQuantity) {
        return webClient.put()
                .uri(buildStockUpdateUri(itemId, stockQuantity))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error updating stock quantity: " + e.getMessage()));
    }

    private String buildStockUpdateUri(Long itemId, int stockQuantity) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:8085")
                .path("/api/internal/stocks/{itemId}")
                .queryParam("stockQuantity", stockQuantity)
                .buildAndExpand(itemId)
                .toUriString();
    }
}
