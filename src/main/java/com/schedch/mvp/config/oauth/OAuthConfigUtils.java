package com.schedch.mvp.config.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;

@Component
@Getter
@RequiredArgsConstructor
public class OAuthConfigUtils {

    private final GoogleConfigUtils googleConfigUtils;
    private final KakaoConfigUtils kakaoConfigUtils;

    public String getSignInAuthUrl(String channel) {
        switch (channel) {
            case "google":
                return googleConfigUtils.googleSignInInitUrl(null);

            case "kakao":
                return kakaoConfigUtils.kakaoSignInitUrl(true);
        }

        throw new InvalidParameterException(String.format("%s는 올바른 OAuth channel이 아닙니다", channel));
    }

    public String getSignOutAuthUrl(String channel) {
        switch (channel) {
            case "google":
                return googleConfigUtils.googleSignOutInitUrl(null);

            case "kakao":
                return kakaoConfigUtils.kakaoSignInitUrl(false);
        }

        throw new InvalidParameterException(String.format("%s는 올바른 OAuth channel이 아닙니다", channel));
    }

    public String getUserCalendarAddAuthUrl(Long userId, String channel) {
        switch (channel) {
            case "google":
                return googleConfigUtils.googleAddCalendarInitUrl(userId);

        }

        throw new InvalidParameterException(String.format("%s는 올바른 OAuth channel이 아닙니다", channel));
    }

    public String getMainPageUrl() throws URISyntaxException {
        return new URI("https://mannatime.io").toString();
    }

    public String getFailurePageUrl() throws URISyntaxException {
        return new URI("https://mannatime.io").toString();
    }
}
