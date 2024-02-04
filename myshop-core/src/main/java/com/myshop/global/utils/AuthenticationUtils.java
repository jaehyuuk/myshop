package com.myshop.global.utils;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.myshop.user.custom.UserDetailsImpl;

@AllArgsConstructor
public class AuthenticationUtils {
    public static Long getUserIdByToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new IllegalStateException("No authenticated user found");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }
}