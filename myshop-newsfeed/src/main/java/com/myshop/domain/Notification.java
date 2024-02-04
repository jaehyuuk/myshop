package com.myshop.domain;

import com.myshop.global.entity.BaseTimeEntity;
import com.myshop.user.domain.User;
import lombok.Builder;
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

    @Enumerated(EnumType.STRING)
    private NotiType type;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "type_id")
    private Long typeId;

    @Builder
    public Notification(Long id, NotiType type, User fromUser, User toUser, Long postId, Long typeId) {
        this.id = id;
        this.type = type;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.postId = postId;
        this.typeId = typeId;
    }
}