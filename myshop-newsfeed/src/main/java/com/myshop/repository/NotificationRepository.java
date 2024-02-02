package com.myshop.repository;

import com.myshop.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Query(value = "INSERT INTO notifications(from_user_id, to_user_id, type, post_id, type_id, created_at, modified_at) " +
                        "VALUES(?1, ?2, ?3, ?4, ?5, now(), now())", nativeQuery = true)
    int mSave(Long fromUserId, Long toUserId, String type, Long postId, Long typeId);

    List<Notification> findByToUserId(Long userId);
    List<Notification> findByFromUserIdIn(List<Long> userIds);
    boolean existsByToUserId(Long userId);
    Optional<Notification> deleteAllByPostId(Long postId);
    Optional<Notification> deleteAllByTypeId(Long typeId);
    Optional<Notification> deleteAllByToUserId(Long userId);
    Optional<Notification> deleteAllByFromUserId(Long userId);
}
