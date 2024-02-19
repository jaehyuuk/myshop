package com.myshop.service;

import com.myshop.global.exception.BadRequestException;
import com.myshop.domain.item.Item;
import com.myshop.domain.item.ReservedItem;
import com.myshop.item.repository.ItemRepository;
import com.myshop.domain.Order;
import com.myshop.domain.OrderItem;
import com.myshop.domain.OrderStatus;
import com.myshop.dto.CreateOrderItemDto;
import com.myshop.dto.OrderDto;
import com.myshop.repository.OrderRepository;
import com.myshop.user.domain.User;
import com.myshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public OrderStatus processOrder(Long orderId) {
        Order order = findByOrderId(orderId);
        if (order.getStatus() == OrderStatus.ORDER) {
            return order.getStatus();
        }
        return orderPayAndUpdateStatus(order);
    }

    private OrderStatus orderPayAndUpdateStatus(Order order) {
        boolean paymentSuccess = Math.random() < 0.8; // 결제 이탈율 20%

        if (!paymentSuccess) {
            order.cancel();
        } else {
            order.updateStatus(OrderStatus.ORDER);
        }
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getStatus();
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
