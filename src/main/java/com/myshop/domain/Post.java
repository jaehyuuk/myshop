package com.myshop.domain;

import com.myshop.global.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(Long id, User user, String content, List<Like> likes, List<Comment> comments) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.likes = likes;
        this.comments = comments;
    }

    public void addLike(Like like) {
        this.likes.add(like);
        if(like.getPost() != this) {
            like.setPost(this);
        }
    }

    public void removeLike(Like like) {
        Iterator<Like> iterator = this.likes.iterator();
        while (iterator.hasNext()) {
            Like e = iterator.next();
            if (like.equals(e)) {
                iterator.remove();
            }
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        if(comment.getPost() != this) {
            comment.setPost(this);
        }
    }

    public void removeComment(Comment comment, Long userId) {
        if(comment.getWriter().getId() != userId) {
            throw new BadRequestException("댓글 삭제는 댓글 작성자만 가능합니다.");
        }

        Iterator<Comment> iterator = this.comments.iterator();
        while (iterator.hasNext()) {
            Comment e = iterator.next();
            if (comment.equals(e)) {
                iterator.remove();
            }
        }
    }
}