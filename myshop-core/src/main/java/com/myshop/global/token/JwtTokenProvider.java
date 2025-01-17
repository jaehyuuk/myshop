package com.myshop.global.token;

import com.myshop.user.custom.UserDetailsImpl;
import com.myshop.user.custom.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final long tokenValidTime = 60 * 60 * 1000L; //토큰 유효시간 -> 60분

    private final UserDetailsServiceImpl userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;


    //객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    //토큰 생성
    public String createToken(String userPk) {
        //adminPk => loginId
        Claims claims = Jwts.claims().setSubject(userPk); //JWT payload 에 저장되는 정보단위
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) //토큰 유효시각 설정
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) //암호화 알고리즘, secret 값 설정
                .compact();
        // Redis에 토큰 저장 및 만료 시간 설정
        String redisKey = "JWT_TOKEN:" + userPk;
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(redisKey, token, tokenValidTime, TimeUnit.MILLISECONDS);

        return token;
    }


    // 인증 성공시 SecurityContextHolder에 저장할 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt Token에서 User PK 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody().getSubject();
    }

    //토큰 유효성, 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    // Request의 Header에서 token 값 가져오기 ("Bearer" 접두사 처리)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 문자열을 반환
        }
        return null;
    }

}