package com.myshop.domain;

import com.myshop.dto.UpdatePasswordDto;
import com.myshop.dto.UpdateUserDto;
import com.myshop.global.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "profile_img")
    private String profileImg;
    @Column(name = "introduce")
    private String introduce;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(Long id, String name, String email, String password, String profileImg, String introduce) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImg = profileImg;
        this.introduce = introduce;
        this.role = Role.USER;
    }

    public void updateUser(UpdateUserDto userDto) {
        if(userDto.getName() != null) this.name = userDto.getName();
        if(userDto.getProfileImg() != null) this.profileImg = userDto.getProfileImg();
        if(userDto.getIntroduce() != null) this.introduce = userDto.getIntroduce();
    }

    public User hashPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
        return this;
    }

    public void checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(plainPassword, this.password)) {
            throw new BadRequestException("패스워드를 확인하세요.");
        }
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto, PasswordEncoder passwordEncoder) {
        String newPassword = updatePasswordDto.getPassword();
        if (!StringUtils.hasText(newPassword)) {
            throw new BadRequestException("비밀번호를 입력해야 합니다.");
        }
        if (passwordEncoder.matches(newPassword, this.password)) {
            throw new BadRequestException("새로운 비밀번호는 이전 비밀번호와 달라야 합니다.");
        }
        this.password = passwordEncoder.encode(newPassword);
    }
}