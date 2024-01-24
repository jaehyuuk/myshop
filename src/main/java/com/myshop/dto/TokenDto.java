package com.myshop.dto;

import lombok.Builder;
import lombok.Getter;
@Getter
public class TokenDto {
    private Long userId;

    @Builder
    public TokenDto(Long userId) {
        this.userId = userId;
    }
}
