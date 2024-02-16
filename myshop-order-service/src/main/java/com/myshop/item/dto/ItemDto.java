package com.myshop.item.dto;

import com.myshop.domain.item.Item;
import com.myshop.domain.item.GeneralItem;
import com.myshop.domain.item.ReservedItem;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ItemDto {
    private Long id;
    private String name;
    private int price;
    private String type; // "GENERAL" 또는 "RESERVED"를 나타내는 필드 추가

    @Builder
    public ItemDto(Long id, String name, int price, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public static ItemDto of(Item item) {
        String type = item instanceof GeneralItem ? "GENERAL" : item instanceof ReservedItem ? "RESERVED" : "UNKNOWN";
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .type(type)
                .build();
    }
}
