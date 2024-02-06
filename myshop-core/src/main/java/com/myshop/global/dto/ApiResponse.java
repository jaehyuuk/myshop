package com.myshop.global.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private T data;
    private LocalDateTime timeStamp;
}
