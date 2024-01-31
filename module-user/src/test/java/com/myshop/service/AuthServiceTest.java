package com.myshop.service;

import com.myshop.domain.User;
import com.myshop.dto.LoginDto;
import com.myshop.dto.RegisterDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.global.jwt.JwtTokenProvider;
import com.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTemplate redisTemplate;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void init() {
        authService = new AuthService(userRepository, bCryptPasswordEncoder, jwtTokenProvider, redisTemplate);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void joinTest() {
        // given
        RegisterDto registerDto = RegisterDto.builder()
                .email("user@example.com")
                .password("password")
                .name("User")
                .build();

        given(userRepository.existsByEmail(registerDto.getEmail())).willReturn(false);

        // when
        authService.join(registerDto);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시도 시 예외 발생 테스트")
    void joinWithExistingEmailTest() {
        // given
        RegisterDto registerDto = RegisterDto.builder()
                .email("existing@example.com")
                .password("password")
                .name("User")
                .build();

        given(userRepository.existsByEmail(registerDto.getEmail())).willReturn(true);

        // when & then
        assertThrows(BadRequestException.class, () -> {
            authService.join(registerDto);
        });
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() {
        // given
        String email = "user@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String jwtToken = "jwtToken";

        LoginDto loginDto = LoginDto.builder()
                .email(email)
                .password(password)
                .build();

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();

        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(jwtTokenProvider.createToken(email)).willReturn(jwtToken);
        given(redisTemplate.opsForValue()).willReturn(valueOperationsMock);

        // when
        TokenResponseDto tokenResponse = authService.login(loginDto);

        // then
        assertNotNull(tokenResponse);
        assertEquals(jwtToken, tokenResponse.getToken());
        verify(valueOperationsMock).set("JWT_TOKEN:" + email, jwtToken);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시도 시 예외 발생 테스트")
    void loginWithNonExistingEmailTest() {
        // given
        LoginDto loginDto = LoginDto.builder()
                .email("nonexisting@example.com")
                .password("password")
                .build();

        given(userRepository.findByEmail(loginDto.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            authService.login(loginDto);
        });
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() {
        // given
        Long userId = 1L;
        String email = "user@example.com";
        User user = User.builder()
                .id(userId)
                .email(email)
                .build();

        // UserRepository를 모의 객체로 설정하여 User 객체를 반환하도록 함
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        String key = "JWT_TOKEN:" + email;
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);

        // Redis와의 상호작용을 모의 객체로 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(key)).thenReturn("jwtToken");

        // when
        authService.logout(userId);

        // then
        verify(redisTemplate).delete(key);
    }
}
