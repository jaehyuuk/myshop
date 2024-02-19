package com.myshop.service;

import com.myshop.domain.Follow;
import com.myshop.domain.NotiType;
import com.myshop.domain.Notification;
import com.myshop.dto.CreateNewsFeedDto;
import com.myshop.global.dto.CreateNotificationDto;
import com.myshop.global.dto.PostResponseDto;
import com.myshop.user.domain.User;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.FollowRepository;
import com.myshop.repository.NotificationRepository;
import com.myshop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class NewsFeedServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NewsFeedService newsFeedService;
    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        newsFeedService = new NewsFeedService(userRepository, followRepository, notificationRepository);
    }

    @Test
    @DisplayName("뉴스피드 조회 테스트")
    void getNewsFeedTest() {
        // given
        Long userId = 1L;
        List<Long> followingIds = List.of(2L, 3L);

        User user = User.builder().id(userId).build();
        List<Follow> follows = followingIds.stream()
                .map(followingId -> Follow.builder()
                        .follower(user)
                        .following(User.builder().id(followingId).build())
                        .build())
                .collect(Collectors.toList());

        List<Notification> notifications = List.of();
        List<PostResponseDto> posts = List.of();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(followRepository.findByFollowerId(userId)).willReturn(follows);
        given(notificationRepository.findByFromUserIdIn(followingIds)).willReturn(notifications);

        // when
        List<CreateNewsFeedDto> newsFeed = newsFeedService.getNewsFeed(userId);

        // then
        assertNotNull(newsFeed);
    }


    @Test
    @DisplayName("존재하지 않는 유저의 알림 조회 시 예외 발생")
    void getNotisForNonExistentUserTest() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            newsFeedService.getMyNotis(userId);
        });
    }

    @Test
    @DisplayName("알림 생성 테스트")
    void createNotificationTest() {
        // given
        Long fromUserId = 1L;
        Long toUserId = 2L;
        String type = NotiType.FOLLOW.name();
        Long postId = 0L;
        Long typeId = 10L;

        CreateNotificationDto request = new CreateNotificationDto();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setType(type);
        request.setPostId(postId);
        request.setTypeId(typeId);

        // when
        newsFeedService.createNotification(request);

        // then
        verify(notificationRepository).mSave(fromUserId, toUserId, type, postId, typeId);
    }


    @Test
    @DisplayName("팔로우 생성 테스트")
    void createNewFollowTest() {
        // given
        Long userId = 1L;
        Long followingId = 2L;

        User user = User.builder().id(userId).build();
        User following = User.builder().id(followingId).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.findById(followingId)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(user, following)).willReturn(Optional.empty());

        // when
        newsFeedService.follow(userId, followingId);

        // then
        verify(followRepository).save(any(Follow.class));
        verify(notificationRepository).mSave(eq(userId), eq(followingId), eq(NotiType.FOLLOW.name()), eq(0L), anyLong());
    }

    @Test
    @DisplayName("팔로우 삭제 테스트")
    void removeFollowTest() {
        // given
        Long userId = 1L;
        Long followingId = 2L;

        User user = User.builder().id(userId).build();
        User following = User.builder().id(followingId).build();
        Follow existingFollow = Follow.builder().follower(user).following(following).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.findById(followingId)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(user, following)).willReturn(Optional.of(existingFollow));

        // when
        newsFeedService.follow(userId, followingId);

        // then
        verify(followRepository).delete(existingFollow);
    }

    @Test
    @DisplayName("자기 자신을 팔로우하는 경우 예외 발생 테스트")
    void followSelfTest() {
        assertThrows(BadRequestException.class, () -> {
            newsFeedService.follow(1L, 1L);
        });
    }

}