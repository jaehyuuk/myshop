package com.myshop.global.filter;

import com.myshop.global.token.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final TokenManager tokenManager;
    private final static String AUTHORIZATION_HEADER = "x-auth-token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, NullPointerException {

        // 1) 토큰 조회
        String accessToken = this.resolveToken(request);

        // 2) 토큰 벨리데이션
        tokenManager.validateToken(accessToken);

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP Header > bearerToken 추출
     *
     * @param request
     */
    private String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        Collection<String> excludeUrlPatterns = new LinkedHashSet<>();

        excludeUrlPatterns.add("/api/users/**");
        excludeUrlPatterns.add("/api/auth/**");

        return excludeUrlPatterns.stream().anyMatch(pattern -> new AntPathMatcher().match(pattern, request.getServletPath()));
    }
}
