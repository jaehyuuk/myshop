package com.myshop.global.exception.handler;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorDetailResponse {
    private String msg;

    @Builder
    public ErrorDetailResponse(String msg) {
        this.msg = msg;
    }
}
