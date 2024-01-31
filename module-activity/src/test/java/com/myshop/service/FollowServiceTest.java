package com.myshop.service;

import com.myshop.domain.Follow;
import com.myshop.domain.NotiType;
import com.myshop.domain.User;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.FollowRepository;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.PostRepository;
import com.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class FollowServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private FollowService followService;
    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        followService = new FollowService(userRepository, postRepository, followRepository, notificationRepository);
    }

    @Test
    @DisplayName("팔로우 추가 테스트")
    void addFollowTest() {
        // given
        Long userId = 1L;
        Long followingId = 2L;

        User user = User.builder().id(userId).build();
        User following = User.builder().id(followingId).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.findById(followingId)).willReturn(Optional.of(following));
        given(followRepository.findByFollowerAndFollowing(user, following)).willReturn(Optional.empty());

        // when
        followService.follow(userId, followingId);

        // then
        verify(followRepository).save(any(Follow.class));
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
        followService.follow(userId, followingId);

        // then
        verify(followRepository).delete(existingFollow);
    }

    @Test
    @DisplayName("자기 자신을 팔로우하는 경우 예외 발생 테스트")
    void followSelfTest() {
        assertThrows(BadRequestException.class, () -> {
            followService.follow(1L, 1L);
        });
    }

}