package com.myshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myshop.domain.item.GeneralItem;
import com.myshop.domain.item.Item;
import com.myshop.domain.item.ReservedItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 아닌 필드만 JSON에 포함
public class ItemDetailDto {
    private Long id;
    private String name;
    private String content;
    private int price;
    private int stockQuantity;
    private String type; // "GENERAL" 또는 "RESERVED"
    private LocalDateTime reservationStart; // 예약 시작 시간
    private LocalDateTime reservationEnd; // 예약 종료 시간

    @Builder
    public ItemDetailDto(Long id, String name, String content, int price, int stockQuantity, String type,
                         LocalDateTime reservationStart, LocalDateTime reservationEnd) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.type = type;
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
    }

    public static ItemDetailDto of(Item item) {
        String type = item instanceof GeneralItem ? "GENERAL" : item instanceof ReservedItem ? "RESERVED" : "UNKNOWN";
        LocalDateTime reservationStart = null;
        LocalDateTime reservationEnd = null;

        if (item instanceof ReservedItem) {
            ReservedItem reservedItem = (ReservedItem) item;
            reservationStart = reservedItem.getReservationStart();
            reservationEnd = reservedItem.getReservationEnd();
        }

        return ItemDetailDto.builder()
                .id(item.getId())
                .name(item.getName())
                .content(item.getContent())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .type(type)
                .reservationStart(reservationStart)
                .reservationEnd(reservationEnd)
                .build();
    }
}
