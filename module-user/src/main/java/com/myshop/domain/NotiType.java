package com.myshop.domain;

import lombok.Getter;

@Getter
public enum NotiType {
    LIKE("좋아") {
        @Override
        public String formatMessage(String fromUserName, String toUserName, Long postId, boolean isMyNotification) {
            String targetUserName = isMyNotification ? "당신의" : toUserName + "님의";
            return String.format("%s님이 %s %d 포스트를 %s합니다.", fromUserName, targetUserName, postId, getKey());
        }
    },
    COMMENT("댓글") {
        @Override
        public String formatMessage(String fromUserName, String toUserName, Long postId, boolean isMyNotification) {
            String targetUserName = isMyNotification ? "당신의" : toUserName + "님의";
            return String.format("%s님이 %s %d 포스트에 %s을 남겼습니다.", fromUserName, targetUserName, postId, getKey());
        }
    },
    FOLLOW("팔로우") {
        @Override
        public String formatMessage(String fromUserName, String toUserName, Long postId, boolean isMyNotification) {
            String targetUserName = isMyNotification ? "당신을" : toUserName + "님을";
            return String.format("%s님이 %s %s합니다.", fromUserName, targetUserName, getKey());
        }
    };

    NotiType(String key) {
        this.key = key;
    }

    private String key;

    public abstract String formatMessage(String fromUserName, String toUserName, Long postId, boolean isMyNotification);

}
