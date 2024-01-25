package com.myshop.domain;

import com.myshop.dto.UpdatePasswordDto;
import com.myshop.dto.UpdateUserDto;
import com.myshop.global.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User  extends BaseTimeEntity{
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

    @Builder
    public User(Long id, String name, String email, String password, String profileImg, String introduce) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImg = profileImg;
        this.introduce = introduce;
    }

    /**
     * 비밀번호를 암호화
     * @param passwordEncoder 암호화 할 인코더 클래스
     * @return 변경된 유저 Entity
     */
    public User hashPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
        return this;
    }

    /**
     * 비밀번호 확인
     * @param plainPassword 암호화 이전의 비밀번호
     * @param passwordEncoder 암호화에 사용된 클래스
     */
    public void checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(plainPassword, this.password)) {
            throw new BadRequestException("패스워드를 확인하세요.");
        }
    }

    public void update(UpdateUserDto userDto) {
        if(userDto.getName() != null) this.name = userDto.getName();
        if(userDto.getProfileImg() != null) this.profileImg = userDto.getProfileImg();
        if(userDto.getIntroduce() != null) this.introduce = userDto.getIntroduce();
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        if(updatePasswordDto.getPassword() != null) this.password = updatePasswordDto.getPassword();
    }
}