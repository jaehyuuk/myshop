package com.myshop.repository;

import com.myshop.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> deleteAllByUserId(Long userId);
}
