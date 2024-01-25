package com.myshop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "follows")
@NoArgsConstructor
@Getter
public class Follow extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    public void setFollower(User follower) {
        this.follower = follower;
        follower.getFollowings().add(this); // Add this follow instance to the follower's followings
    }

    public void setFollowing(User following) {
        this.following = following;
        following.getFollowers().add(this); // Add this follow instance to the following's followers
    }
}