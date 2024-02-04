package com.myshop.repository;

import com.myshop.domain.Follow;
import com.myshop.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollowerId(Long followerId);
    Optional<Follow> deleteAllByFollowerId(Long followerId);
    Optional<Follow> deleteAllByFollowingId(Long followerId);
}
