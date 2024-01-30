package com.myshop.service;

import com.myshop.domain.NotiType;
import com.myshop.domain.Notification;
import com.myshop.domain.User;
import com.myshop.dto.NotificationDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationService notificationService;
    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        notificationService = new NotificationService(notificationRepository, userRepository);
    }

    @Test
    @DisplayName("알림 목록 조회 테스트")
    void getNotisTest() {
        // given
        Long userId = 1L;
        Long fromUserId = 2L;

        User user = User.builder().id(userId).build();
        User fromUser = User.builder().id(fromUserId).build();

        Notification notification1 = Notification.builder()
                .type(NotiType.COMMENT)
                .fromUser(fromUser)
                .toUser(user)
                .typeId(1L)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        Notification notification2 = Notification.builder()
                .type(NotiType.LIKE)
                .fromUser(fromUser)
                .toUser(user)
                .typeId(2L)
                .createdAt(LocalDateTime.now())
                .build();

        List<Notification> notifications = Arrays.asList(notification1, notification2);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(notificationRepository.existsByToUserId(userId)).willReturn(true);
        given(notificationRepository.findByToUserId(userId)).willReturn(notifications);

        // when
        List<NotificationDto> result = notificationService.getNotis(userId);

        // then
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedAt().isAfter(result.get(1).getCreatedAt()));
    }



    @Test
    @DisplayName("존재하지 않는 유저의 알림 조회 시 예외 발생")
    void getNotisForNonExistentUserTest() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            notificationService.getNotis(userId);
        });
    }


    @Test
    @DisplayName("다른 사용자 알림 메시지 포맷 테스트")
    void getNotificationTest() {
        // given
        User fromUser = User.builder().name("Alice").build();
        User toUser = User.builder().name("Bob").build();

        Notification notification = Notification.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .type(NotiType.FOLLOW)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        NotificationDto notificationDto = NotificationDto.getFollowNotification(notification);

        // then
        String expectedMessage = "Alice님이 Bob님을 팔로우합니다.";
        assertEquals(expectedMessage, notificationDto.getMessage());
        assertNotNull(notificationDto.getCreatedAt());
    }

    @Test
    @DisplayName("현재 사용자 알림 메시지 포맷 테스트")
    void getMyNotificationTest() {
        // given
        User fromUser = User.builder().name("Alice").build();

        Notification notification = Notification.builder()
                .fromUser(fromUser)
                .type(NotiType.COMMENT)
                .typeId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        NotificationDto notificationDto = NotificationDto.getMyNotification(notification);

        //then
        String expectedMessage = "Alice님이 당신의 1 포스트에 댓글을 남겼습니다.";
        assertEquals(expectedMessage, notificationDto.getMessage());
        assertNotNull(notificationDto.getCreatedAt());
    }
}