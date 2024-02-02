package com.myshop.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "likes")
@NoArgsConstructor
public class Like extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "user_id")
    private Long userId;

    @Builder
    public Like(Long id, Post post, Long userId) {
        this.id = id;
        this.post = post;
        this.userId = userId;
    }

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getLikes().remove(this);
        }
        this.post = post;

        if (!post.getLikes().contains(this)) {
            post.getLikes().add(this);
        }
    }

}