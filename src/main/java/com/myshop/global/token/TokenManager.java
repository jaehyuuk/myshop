package com.myshop.global.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.myshop.dto.TokenDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.global.exception.UsernameFromTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenManager {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.issuer}")
    private String jwtIssuer;

    public final static Long LOCAL_ACCESS_TOKEN_TIME_OUT = 9999999L * 60 * 60;
    public final static Long ACCESS_TOKEN_TIME_OUT = 1000L * 60 * 60; // 1시간

    public TokenResponseDto generateToken(TokenDto tokenDto) {
        String token = newToken(tokenDto, LOCAL_ACCESS_TOKEN_TIME_OUT);
        return new TokenResponseDto(token);
    }

    private String newToken(TokenDto token, Long expireTime) {
        return JWT.create()
                .withClaim("user_id", token.getUserId())
                .withIssuedAt(new Date())
                .withIssuer(jwtIssuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    /**
     * Jwt Token 검증한다.
     * 토큰 만료시 우선 true로 리턴하고 이후 로직에서 RefreshToken을 검증한다.
     *
     * @param token
     */
    public void validateToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            throw new UsernameFromTokenException("JWT Empty. Please check header.");
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(jwtIssuer)
                .build();

        DecodedJWT jwt = verifier.verify(token);

        TokenContext context = TokenContextHolder.getContext();

        Claim claim = jwt.getClaim("user_id");
        context.setUserId(claim.asLong());

        TokenContextHolder.setContext(context);
    }
}
