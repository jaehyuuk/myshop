package com.myshop.domain;

import lombok.Getter;

@Getter
public enum NotiType {
    LIKE("좋아요"), COMMENT("댓글"), FOLLOW("팔로우");

    NotiType(String key) {
        this.key = key;
    }

    private String key;
}
