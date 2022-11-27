package com.schedch.mvp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.schedch.mvp.model.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@PropertySource("classpath:jwt.yaml")
public class JwtConfig {

    @Value("${secret}")
    private String SECRET; // 우리 서버만 알고 있는 비밀값

    @Value("${expiration.time}")
    private Long EXPIRATION_TIME; // 10분 (1000: millisecond -> second)

    @Value("${expiration.time.refresh}")
    private Long REFRESH_EXPIRATION_TIME;

    @Value("${token.prefix}")
    private String TOKEN_PREFIX;

    @Value("${header.string}")
    private String HEADER_STRING;

    @Value("${front.success.redirect}")
    private String frontSuccessRedirect;

    @Value("${front.calendar.grant.redirect}")
    private String frontCalendarGrantRedirect;

    public String createAccessTokenByUser(User user) {
        String jwtToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + getEXPIRATION_TIME())) //10분
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(getSECRET()));

        return jwtToken;
    }

    public String createRefreshToken() {
        String refreshToken = JWT.create()
                .withSubject("refresh")
                .withExpiresAt(new Date(System.currentTimeMillis() + getREFRESH_EXPIRATION_TIME()))
                .sign(Algorithm.HMAC512(getSECRET()));

        return refreshToken;
    }

}
