package com.myshop.global.exception.handler;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ErrorResponse {
    private List<ErrorDetailResponse> errors;

    @Builder
    public ErrorResponse(List<ErrorDetailResponse> errors) {
        this.errors = errors;
    }

    public ErrorResponse(ErrorDetailResponse error) {
        this.errors = Arrays.asList(error);
    }
}
