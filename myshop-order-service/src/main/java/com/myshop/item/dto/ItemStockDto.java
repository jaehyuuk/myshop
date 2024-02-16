package com.myshop.item.dto;

import com.myshop.domain.item.Item;
import com.myshop.domain.item.ReservedItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemStockDto {
    private Long id;
    private int stockQuantity;
    private String name;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;

    public static ItemStockDto of(Item item) {
        LocalDateTime reservationStart = null;
        LocalDateTime reservationEnd = null;

        if (item instanceof ReservedItem) {
            ReservedItem reservedItem = (ReservedItem) item;
            reservationStart = reservedItem.getReservationStart();
            reservationEnd = reservedItem.getReservationEnd();
        }
        return ItemStockDto.builder()
                .id(item.getId())
                .name(item.getName())
                .stockQuantity(item.getStockQuantity())
                .reservationStart(reservationStart)
                .reservationEnd(reservationEnd)
                .build();
    }
}
