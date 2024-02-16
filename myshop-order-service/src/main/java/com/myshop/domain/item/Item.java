package com.myshop.domain.item;

import com.myshop.global.exception.BadRequestException;
import com.myshop.item.dto.UpdateItemDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "items")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor
@SuperBuilder
public abstract class Item {
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
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    public void updateItem(UpdateItemDto itemDto) {
        if(itemDto.getName() != null) this.name = itemDto.getName();
        if(itemDto.getContent() != null) this.content = itemDto.getContent();
        if(itemDto.getPrice() != null) this.price = itemDto.getPrice();
        if(itemDto.getStockQuantity() != null) this.stockQuantity = itemDto.getStockQuantity();
        this.modifiedAt = LocalDateTime.now();

        if (this instanceof ReservedItem) {
            ReservedItem reserved = (ReservedItem) this;
            reserved.updateReservationTimes(itemDto.getReservationStart(), itemDto.getReservationEnd());
        }
    }

    // stock 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // stock 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new BadRequestException("재고가 부족합니다.");
        }
        this.stockQuantity = restStock;
    }
}
