package com.myshop.global.config;

import com.myshop.global.filter.JwtAuthenticationFilter;
import com.myshop.global.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // BCryptPasswordEncoder Bean 정의
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 정의
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // WebSecurity 설정
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/css/**", "/static/js/**", "*.ico", // 정적 리소스 무시
                "/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
                "/swagger-ui.html", "/webjars/**", "/swagger/**"); // Swagger 경로 무시
    }

    // HttpSecurity 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF 보호 비활성화
                .authorizeRequests()
                .antMatchers("/api/auth/join",
                        "/api/auth/login",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/api/internal/**")
                .permitAll() // 인증 필요 없는 URL 설정
                .antMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 정책: STATELESS
                .and()
                .formLogin().disable() // 폼 로그인 비활성화
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터 추가
    }
}
