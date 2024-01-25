package com.myshop.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(fetch= FetchType.EAGER, optional = false)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(name = "content")
    private String content;

    @Builder
    public Comment(Long id, Post post, User writer, String content) {
        this.id = id;
        this.post = post;
        this.writer = writer;
        this.content = content;
    }
}
