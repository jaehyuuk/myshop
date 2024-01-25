package com.myshop.global.config;

import com.myshop.global.filter.ExceptionHandlerFilter;
import com.myshop.global.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@EnableWebSecurity
@RequiredArgsConstructor
@Order(300)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenFilter jwtTokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    /**
     * PasswordEncoder를 Bean으로 등록
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/**").permitAll()
                .antMatchers(
                        "/",
                        "/ping",
                        "/profile",
                        "/webTest",
                        "/active",
                        "/favicon.ico",
                        "/common/**",
                        "/open-api/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html")
                .permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .antMatcher("/**")
                .addFilterBefore(jwtTokenFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtTokenFilter.class);
    }
}
