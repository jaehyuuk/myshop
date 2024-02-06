package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.*;
import com.myshop.global.dto.ApiResponse;
import com.myshop.global.dto.PostResponseDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.FollowRepository;
import com.myshop.repository.NotificationRepository;
import com.myshop.user.repository.UserRepository;
import com.myshop.user.domain.User;
import com.myshop.global.dto.CreateNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final WebClient webClient;

    @Transactional
    public void follow(Long userId, Long followingId) {
        validateSelfFollow(userId, followingId);
        User user = findUserById(userId);
        User following = findUserById(followingId);

        followRepository.findByFollowerAndFollowing(user, following)
                .ifPresentOrElse(
                        this::unfollowExistingFollow, // 이미 팔로우 했다면 삭제
                        () -> createAndSaveNewFollow(user, following) // 아니라면 팔로우
                );
    }

    @Transactional(readOnly = true)
    public List<FollowDto> getFollows(Long userId) {
        findUserById(userId);
        return followRepository.findByFollowerId(userId).stream()
                .map(FollowDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CreateNewsFeedDto> getNewsFeed(Long userId) {
        validateUser(userId);
        List<Long> followingIds = getFollowingIds(userId);
        List<NotificationDto> notificationDtos = getNotifications(followingIds);
        List<PostResponseDto> postDtos = getPosts(followingIds);

        return List.of(new CreateNewsFeedDto(notificationDtos, postDtos));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotis(Long userId) {
        validateUser(userId);
        return notificationRepository.findByToUserId(userId).stream()
                .filter(noti -> !noti.getFromUser().getId().equals(userId)) // 본인이 보낸 알림 제외
                .map(NotificationDto::getMyNotification)
                .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
    }

    private void validateSelfFollow(Long userId, Long followingId) {
        if (userId.equals(followingId)) {
            throw new BadRequestException("사용자는 자신을 팔로우할 수 없습니다.");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
    }

    private void unfollowExistingFollow(Follow follow) {
        Long followId = follow.getId();
        followRepository.delete(follow);
        notificationRepository.deleteAllByTypeId(followId);
    }

    private void createAndSaveNewFollow(User user, User following) {
        Follow follow = Follow.builder()
                .follower(user)
                .following(following)
                .build();
        follow = followRepository.save(follow);
        notificationRepository.mSave(user.getId(), following.getId(), NotiType.FOLLOW.name(), 0L, follow.getId());
    }

    private List<Long> getFollowingIds(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(follow -> follow.getFollowing().getId())
                .collect(Collectors.toList());
    }

    private List<NotificationDto> getNotifications(List<Long> followingIds) {
        return notificationRepository.findByFromUserIdIn(followingIds).stream()
                .map(NotificationDto::getFollowNotification)
                .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    // RestApi
    private List<PostResponseDto> getPosts(List<Long> followingIds) {
        WebClient webClient = WebClient.create("http://localhost:8082");

        Mono<ApiResponse> apiResponseMono = webClient.post()
                .uri("/api/internal/posts/follows")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(followingIds)
                .retrieve()
                .bodyToMono(ApiResponse.class);

        ApiResponse apiResponse = apiResponseMono.block(); // 블로킹 호출로 결과를 기다림

        return apiResponse.getData();
    }

    @Transactional
    public void deleteNewsfeedByUserId(Long userId) {
        notificationRepository.deleteAllByToUserId(userId);
        notificationRepository.deleteAllByFromUserId(userId);
        followRepository.deleteAllByFollowerId(userId);
        followRepository.deleteAllByFollowingId(userId);
    }

    @Transactional
    public void createNotification(CreateNotificationDto request) {
        notificationRepository.mSave(request.getFromUserId(), request.getToUserId(),
                request.getType(), request.getPostId(), request.getTypeId());
    }

    @Transactional
    public void deleteNotificationByTypeId(Long typeId) {
        notificationRepository.deleteAllByTypeId(typeId);
    }

    @Transactional
    public void deleteNotificationByPostId(Long postId) {
        notificationRepository.deleteAllByPostId(postId);
    }
}
