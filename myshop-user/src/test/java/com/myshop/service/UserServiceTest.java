package com.myshop.service;

import com.myshop.domain.User;
import com.myshop.dto.UpdatePasswordDto;
import com.myshop.dto.UpdateUserDto;
import com.myshop.dto.UserDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder bCryptPasswordEncoder;
    @Mock
    private RedisTemplate redisTemplate;
    @InjectMocks
    private UserService userService;
    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository, bCryptPasswordEncoder, redisTemplate);
    }

    @Test
    @DisplayName("유저 전체 조회 정보를 리턴한다.")
    void getUsers() {
        // given
        List<User> mockUsers = Arrays.asList(
                User.builder()
                        .id(1L)
                        .name("User1")
                        .email("user1@example.com")
                        .profileImg("profile1.jpg")
                        .introduce("Introduce 1")
                        .build(),
                User.builder()
                        .id(2L)
                        .name("User2")
                        .email("user2@example.com")
                        .profileImg("profile2.jpg")
                        .introduce("Introduce 2")
                        .build()
        );
        given(userRepository.findAll()).willReturn(mockUsers);

        // when
        List<UserDto> userDtos = userService.getUsers();

        // then
        assertNotNull(userDtos);
        assertEquals(mockUsers.size(), userDtos.size());
        assertEquals(mockUsers.get(0).getName(), userDtos.get(0).getName());
        assertEquals(mockUsers.get(1).getName(), userDtos.get(1).getName());
    }

    @Test
    @DisplayName("특정 ID를 가진 유저 정보 가져오기 테스트")
    void getUserByIdTest() {
        // given
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .name("User1")
                .email("user1@example.com")
                .profileImg("profile1.jpg")
                .introduce("Introduce 1")
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // when
        UserDto userDto = userService.getUserById(userId);

        // then
        assertNotNull(userDto);
        assertEquals(mockUser.getName(), userDto.getName());
        assertEquals(mockUser.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 정보를 가져올 때 예외 발생 테스트")
    void getUserByIdNotFoundTest() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BadRequestException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    @DisplayName("유저 정보 업데이트 테스트")
    void updateUserTest() {
        // given
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .name("User1")
                .email("user1@example.com")
                .profileImg("profile1.jpg")
                .introduce("Introduce 1")
                .build();
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .name("User1 Updated")
                .profileImg("updatedProfile1.jpg")
                .introduce("Updated Introduce 1")
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // when
        userService.updateUser(userId, updateUserDto);

        // then
        verify(userRepository).save(mockUser);
        assertEquals(updateUserDto.getName(), mockUser.getName());
        assertEquals(updateUserDto.getProfileImg(), mockUser.getProfileImg());
        assertEquals(updateUserDto.getIntroduce(), mockUser.getIntroduce());
    }

    @Test
    @DisplayName("비밀번호 갱신 테스트 - 사용자 찾지 못함")
    void updatePassword_UserNotFound_Test() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setPassword("newPassword");

        // when & then
        assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(userId, updatePasswordDto);
        });
    }

    @Test
    @DisplayName("비밀번호 갱신 테스트 - 유효한 갱신")
    void updatePassword_ValidUpdate_Test() {
        // given
        Long userId = 1L;
        String oldPasswordEncoded = "oldPasswordEncoded";
        String newPassword = "newPassword";
        User mockUser = User.builder()
                .id(userId)
                .name("Test")
                .email("test@example.com")
                .password(oldPasswordEncoded) // 초기화된 암호화된 이전 비밀번호
                .build();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setPassword(newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(bCryptPasswordEncoder.encode(newPassword)).willReturn("encodedNewPassword");
        given(bCryptPasswordEncoder.matches(newPassword, oldPasswordEncoded)).willReturn(false);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("token");

        // when
        userService.updatePassword(userId, updatePasswordDto);

        // then
        verify(redisTemplate).delete("JWT_TOKEN:test@example.com");
        assertEquals("encodedNewPassword", mockUser.getPassword());
    }

    @Test
    @DisplayName("비밀번호 갱신 테스트 - 기존 비밀번호와 동일")
    void updatePassword_SameAsOldPassword_Test() {
        // given
        Long userId = 1L;
        String oldPasswordEncoded = "oldPasswordEncoded";
        String oldPassword = "oldPassword";
        User mockUser = User.builder()
                .id(userId)
                .name("Test")
                .email("test@example.com")
                .password(oldPasswordEncoded) // 초기화된 암호화된 이전 비밀번호
                .build();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setPassword(oldPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(bCryptPasswordEncoder.matches(oldPassword, oldPasswordEncoded)).willReturn(true);

        // when & then
        assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(userId, updatePasswordDto);
        });
    }


//    @Test
//    @DisplayName("유저 정보를 삭제한다.")
//    void deleteUser() {
//        // given
//        Long userId = 1L;
//        String email = "user@example.com";
//        User user = User.builder()
//                .id(userId)
//                .email(email)
//                .build();
//
//        String key = "JWT_TOKEN:" + email;
//        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
//        when(valueOperationsMock.get(key)).thenReturn("jwtToken");
//
//        // when
//        userService.deleteUser(userId);
//
//        // then
//        verify(postRepository).deleteAllByUser(user);
//        verify(userRepository).delete(user);
//        verify(redisTemplate).opsForValue();
//        verify(valueOperationsMock).get(key);
//        verify(redisTemplate).delete(key);
//    }
//
//    @Test
//    @DisplayName("유저 삭제 시 유저가 없는 경우 예외 발생 테스트")
//    void deleteUserWhenUserNotFoundTest() {
//        // given
//        Long userId = 1L;
//        given(userRepository.findById(userId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThrows(BadRequestException.class, () -> {
//            userService.deleteUser(userId);
//        });
//    }
}