package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.FollowRepository;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        if (userId.equals(followingId)) {
            throw new BadRequestException("사용자는 자신을 팔로우할 수 없습니다.");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        User following = userRepository.findById(followingId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(user, following);
        if (existingFollow.isPresent()) {
            // 이미 팔로우한 경우
            Long followId = existingFollow.get().getId();
            followRepository.delete(existingFollow.get());
            notificationRepository.deleteAllByTypeId(followId);
        } else { // 아닌 경우 팔로우
            Follow follow = Follow.builder()
                    .follower(user)  // 팔로워 설정
                    .following(following)  // 팔로잉 대상 설정
                    .build();
            followRepository.save(follow);
            notificationRepository.mSave(userId, followingId, NotiType.FOLLOW.name(), 0L, follow.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<FollowDto> getFollows(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("유저 정보를 찾을 수 없습니다.");
        }

        // 해당 사용자를 팔로우하는 모든 Follow 엔티티 가져오기
        List<Follow> followingUsers = followRepository.findByFollowerId(userId);

        // Follow 엔티티 리스트를 FollowDto 리스트로 변환
        return followingUsers.stream()
                .map(FollowDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NewsFeedDto> getFeeds(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );

        // 현재 사용자가 팔로우하는 사용자들의 ID 목록
        List<Long> followingIds = followRepository.findByFollowerId(userId).stream()
                .map(follow -> follow.getFollowing().getId())
                .collect(Collectors.toList());

        // 현재 사용자가 팔로우하는 사용자들의 활동 (알림) 가져오기
        List<Notification> notifications = notificationRepository.findByFromUserIdIn(followingIds);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(NotificationDto::getFollowNotification)
                .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // 현재 사용자가 팔로우하는 사용자들의 게시물 가져오기
//        List<PostResponseDto> postDtos = webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .scheme("http")
//                        .host("localhost")
//                        .port(8082)
//                        .path("/api/internal/posts/users")
//                        .queryParam("userIds", followingIds)
//                        .build())
//                .retrieve()
//                .bodyToFlux(PostResponseDto.class)
//                .collectList()
//                .block();

        log.info("Preparing to send GET request with userIds: {}", followingIds);
        List<PostResponseDto> postDtos = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8082)
                        .path("/api/internal/posts/users")
                        .queryParam("userIds", followingIds)
                        .build())
                .retrieve()
                .bodyToFlux(PostResponseDto.class)
                .doOnSubscribe(subscription -> log.info("Request subscribed"))
                .doOnNext(post -> log.info("Received post DTO: {}", post))
                .doOnComplete(() -> log.info("Request completed successfully"))
                .doOnError(error -> log.error("Error occurred during request: ", error))
                .collectList()
                .doOnSubscribe(subscription -> log.info("Collecting PostResponseDto list"))
                .block();
        log.info("Successfully retrieved PostResponseDtos: {}", postDtos);

        return List.of(new NewsFeedDto(notificationDtos, postDtos));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotis(Long userId) {
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

    // RestApi
    @Transactional
    public void deleteNewsfeedByUserId(Long userId) {
        notificationRepository.deleteAllByToUserId(userId);
        notificationRepository.deleteAllByFromUserId(userId);
        followRepository.deleteAllByFollowerId(userId);
        followRepository.deleteAllByFollowingId(userId);
    }

    @Transactional
    public void createNotification(NotificationCreateRequest request) {
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
