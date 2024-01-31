package com.myshop.controller;

import com.myshop.dto.NotificationDto;
import com.myshop.global.utils.AuthenticationUtils;
import com.myshop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notis")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> getNotis() {
        Long userId = AuthenticationUtils.getUserIdByToken();
        return notificationService.getNotis(userId);
    }
}
