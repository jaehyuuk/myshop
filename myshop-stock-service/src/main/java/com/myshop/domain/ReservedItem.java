package com.myshop.domain;

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

    public void updateReservationTimes(LocalDateTime reservationStart, LocalDateTime reservationEnd) {
        if (reservationStart != null && reservationEnd != null) {
            this.reservationStart = reservationStart;
            this.reservationEnd = reservationEnd;
        }
    }

    public boolean isReservationActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(reservationStart) && now.isBefore(reservationEnd);
    }
}
