package com.myshop.service;

import com.myshop.domain.Follow;
import com.myshop.domain.User;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.FollowRepository;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

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
        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            throw new BadRequestException("이미 팔로우한 유저입니다.");
        }
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(
                () -> new BadRequestException("팔로워 사용자를 찾을 수 없습니다.")
        );
        User following = userRepository.findById(followingId).orElseThrow(
                () -> new BadRequestException("팔로잉 사용자를 찾을 수 없습니다.")
        );
        if (!followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            throw new BadRequestException("없는 유저입니다.");
        }
        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

}
