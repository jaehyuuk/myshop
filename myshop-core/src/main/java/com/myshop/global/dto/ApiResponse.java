package com.myshop.global.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ApiResponse {
    private boolean success;
    private int status;
    private List<PostResponseDto> data;
    private LocalDateTime timeStamp;
}
