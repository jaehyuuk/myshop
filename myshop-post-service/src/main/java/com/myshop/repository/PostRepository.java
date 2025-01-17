package com.myshop.repository;

import com.myshop.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> deleteAllByUserId(Long userId);
    List<Post> findByUserIdIn(List<Long> userIds);
}