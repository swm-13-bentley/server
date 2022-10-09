package com.schedch.mvp.dto.oauth;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.schedch.mvp.model.UserCalendar;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleLoginDto {
    private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String hd;
    private String email;
    private String emailVerified;
    private String atHash;
    private String name;
    private String picture;
    private String givenName;
    private String familyName;
    private String locale;
    private String iat;
    private String exp;
    private String alg;
    private String kid;
    private String typ;
    private String accessToken;
    private String expiresIn;
    private String refreshToken;
    private String scope;
    private String password;

    public TokenResponse toTokenResponse() {
        TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .setTokenType(typ);

        return tokenResponse;

        //없어도 동작함
//                .setExpiresInSeconds(Long.parseLong(expiresIn))
//                .setScope(scope);

    }

    public UserCalendar toUserCalendar() {
        UserCalendar userCalendar = UserCalendar.builder()
                .calendarEmail(this.email)
                .mainCalendar(false)
                .calendarChannel("google")
                .calendarAccessToken(accessToken)
                .calendarRefreshToken(refreshToken)
                .build();
        return userCalendar;
    }
}
