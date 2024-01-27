package com.myshop.repository;

import com.myshop.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Modifying
    @Query(value = "INSERT INTO notifications(from_user_id, to_user_id, type, created_at) VALUES(?1, ?2, ?3, now())", nativeQuery = true)
    int mSave(Long fromUserId, Long toUserId, String type);

    Notification findByToUserId(Long userId);
    boolean existsByToUserId(Long userId);
}
