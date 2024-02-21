package com.myshop.service;

import com.myshop.domain.Order;
import com.myshop.domain.OrderStatus;
import com.myshop.domain.item.Item;
import com.myshop.domain.item.ReservedItem;
import com.myshop.order.repository.OrderRepository;
import com.myshop.order.service.OrderService;
import com.myshop.user.domain.User;
import com.myshop.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 처리")
    void processOrderTest() {
        User user = new User();
        Item item = new ReservedItem();
        Order order = new Order();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderStatus status = orderService.processOrder(1L, 1L);
        assertNotNull(status);
        verify(orderRepository).save(any(Order.class));
    }
}
