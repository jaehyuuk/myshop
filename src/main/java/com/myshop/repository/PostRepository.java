package com.myshop.repository;

import com.myshop.domain.Post;
import com.myshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> deleteAllByUser(User user);
    Post findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}