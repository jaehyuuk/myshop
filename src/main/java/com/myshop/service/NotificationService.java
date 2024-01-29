package com.myshop.service;

import com.myshop.domain.Notification;
import com.myshop.dto.NotificationDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotis(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
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
