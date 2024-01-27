package com.myshop.service;

import com.myshop.domain.Notification;
import com.myshop.dto.NotificationDto;
import com.myshop.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotis(Long userId) {
        List<Notification> notis = new ArrayList<>();
        if (notificationRepository.existsByToUserId(userId)) { // notification
            List<Notification> notifications = notificationRepository.findByToUserId(userId);
            for (Notification noti : notifications) {
                if (!noti.getFromUser().getId().equals(userId)) { // 본인 제외
                    notis.add(noti);
                }
            }
        }
        return notis.stream()
                .map(NotificationDto::getMyNotification)
                .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}
