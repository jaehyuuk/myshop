package com.myshop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "notifications")
@NoArgsConstructor
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name="to_user_id")
    private User toUser;

    private String message;
}