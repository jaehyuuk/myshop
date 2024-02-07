package com.myshop.order.domain;

import com.myshop.global.entity.BaseTimeEntity;
import com.myshop.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, OrderStatus status) {
        this.user = user;
        this.status = status;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        if (orderItem.getOrder() != this) {
            orderItem.changeOrder(this);
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        if (orderItem.getOrder() == this) {
            orderItem.changeOrder(null);
        }
    }
}
