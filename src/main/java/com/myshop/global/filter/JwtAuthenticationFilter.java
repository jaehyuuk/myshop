package com.myshop.global.filter;

import com.myshop.global.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String token = jwtTokenProvider.resolveToken(httpRequest);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String key = "JWT_TOKEN:" + jwtTokenProvider.getUserPk(token);
                String storedToken = redisTemplate.opsForValue().get(key);

                if (redisTemplate.hasKey(key) && storedToken != null) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("토큰이 유효하여 사용자 {}에 대한 인증이 설정되었습니다.", jwtTokenProvider.getUserPk(token));
                } else {
                    log.warn("요청에서 토큰은 발견되었지만 Redis에서 찾을 수 없거나 null입니다. 토큰이 만료되었거나 유효하지 않을 수 있습니다.");
                }
            } else {
                log.warn("요청에서 토큰을 찾을 수 없거나 토큰이 유효하지 않습니다.");
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JwtAuthenticationFilter 처리 중 오류가 발생했습니다: {}", e.getMessage());
            throw new ServletException("인증 처리 중 오류가 발생했습니다", e);
        }
    }
}