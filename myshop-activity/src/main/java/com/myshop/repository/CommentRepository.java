package com.myshop.repository;

import com.myshop.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> deleteAllByWriterId(Long userId);
}
