package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.FollowDto;
import com.myshop.dto.NewsFeedDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.FollowRepository;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.PostRepository;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BadRequestException("사용자는 자신을 팔로우할 수 없습니다.");
        }
        User follower = userRepository.findById(followerId).orElseThrow(
                () -> new BadRequestException("팔로워 사용자를 찾을 수 없습니다.")
        );
        User following = userRepository.findById(followingId).orElseThrow(
                () -> new BadRequestException("팔로잉 사용자를 찾을 수 없습니다.")
        );
        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) { // 이미 팔로운 경우 언팔
            followRepository.findByFollowerAndFollowing(follower, following)
                    .ifPresent(followRepository::delete);
        } else { // 아닌 경우 팔로우
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.save(follow);
            notificationRepository.mSave(followerId, followingId, NotiType.FOLLOW.name(), follow.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<FollowDto> getFollows(Long userId) {
        User follower = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("팔로워 사용자를 찾을 수 없습니다.")
        );
        return follower.getFollowers().stream().map(FollowDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NewsFeedDto> getFeeds(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("팔로워 사용자를 찾을 수 없습니다.")
        );
        List<Long> followingIds = followRepository.findByFollowerId(userId).stream()
                .map(follow -> follow.getFollowing().getId())
                .collect(Collectors.toList());
        List<Notification> notis = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < followingIds.size(); i++) {
            if(notificationRepository.existsByToUserId(followingIds.get(i))){ // notification
                List<Notification> notifications = notificationRepository.findByToUserId(followingIds.get(i));
                for (Notification noti : notifications) { notis.add(noti);}
            }
            if(postRepository.existsByUserId(followingIds.get(i))){ // post
                List<Post> postList = postRepository.findByUserId(followingIds.get(i));
                for (Post post : postList) { posts.add(post);}
            }
        }
        List<NewsFeedDto> newsFeedDtos = new ArrayList<>();
        newsFeedDtos.add(NewsFeedDto.getNewsfeedDto(notis,posts));
        return newsFeedDtos;
    }

}
