package com.myshop.domain.item;

import com.myshop.dto.ItemUpdateDto;
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

    public void updateItem(ItemUpdateDto itemDto) {
        if(itemDto.getName() != null) this.name = itemDto.getName();
        if(itemDto.getContent() != null) this.content = itemDto.getContent();
        if(itemDto.getPrice() != null) this.price = itemDto.getPrice();
        if(itemDto.getStockQuantity() != null) this.stockQuantity = itemDto.getStockQuantity();
        this.modifiedAt = LocalDateTime.now();
    }
}
