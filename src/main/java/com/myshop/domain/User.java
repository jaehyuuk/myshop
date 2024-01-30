package com.myshop.domain;

import com.myshop.dto.UpdatePasswordDto;
import com.myshop.dto.UpdateUserDto;
import com.myshop.global.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> fromUser = new ArrayList<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> toUsers = new ArrayList<>();

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

    public User hashPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
        return this;
    }

    public void checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(plainPassword, this.password)) {
            throw new BadRequestException("패스워드를 확인하세요.");
        }
    }

    public void updateUser(UpdateUserDto userDto) {
        if(userDto.getName() != null) this.name = userDto.getName();
        if(userDto.getProfileImg() != null) this.profileImg = userDto.getProfileImg();
        if(userDto.getIntroduce() != null) this.introduce = userDto.getIntroduce();
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        if(updatePasswordDto.getPassword() != null) this.password = updatePasswordDto.getPassword();
    }
}