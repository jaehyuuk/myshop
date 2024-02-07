package com.myshop.order.service;

import com.myshop.global.exception.BadRequestException;
import com.myshop.item.domain.Item;
import com.myshop.item.repository.ItemRepository;
import com.myshop.order.domain.Order;
import com.myshop.order.domain.OrderItem;
import com.myshop.order.domain.OrderStatus;
import com.myshop.order.dto.CreateOrderItemDto;
import com.myshop.order.dto.OrderDto;
import com.myshop.order.repository.OrderRepository;
import com.myshop.user.domain.User;
import com.myshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createOrder(Long userId, List<CreateOrderItemDto> orderItemDtos) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.ORDER)
                .build();

        for (CreateOrderItemDto dto : orderItemDtos) {
            Item item = itemRepository.findById(dto.getItemId()).orElseThrow(
                    () -> new BadRequestException("상품이 존재하지 않습니다.")
            );
            OrderItem orderItem = dto.toEntity(item);
            order.addOrderItem(orderItem);
        }
        orderRepository.save(order);
        return order.getId();
    }

    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderDto::of).collect(Collectors.toList());
    }
}
