package com.myshop.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "content")
    private String content;
    @Column(name = "price")
    private int price;
    @Column(name = "stock_quantity")
    private int stockQuantity;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
}
