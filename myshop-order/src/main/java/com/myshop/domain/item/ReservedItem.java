package com.myshop.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter
@DiscriminatorValue("RESERVED")
@NoArgsConstructor
@SuperBuilder
public class ReservedItem extends Item {
    @Column(name = "reservation_start")
    private LocalDateTime reservationStart;

    @Column(name = "reservation_end")
    private LocalDateTime reservationEnd;

    public void updateReservationTimes(LocalDateTime start, LocalDateTime end) {
        this.reservationStart = start;
        this.reservationEnd = end;
    }

    public boolean isReservationActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(reservationStart) && now.isBefore(reservationEnd);
    }
}
