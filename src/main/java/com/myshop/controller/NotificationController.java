package com.myshop.controller;

import com.myshop.dto.NotificationDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getNotis() {
        TokenContext context = TokenContextHolder.getContext();
        Long userId = context.getUserId();
        return notificationService.getNotis(userId);
    }
}
