package com.schedch.mvp.config;

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

    public String getAuthUrl(String channel) {
        if(channel.equals("google")) {
            return googleConfigUtils.googleInitUrl(null);
        }
        else {
            throw new InvalidParameterException(String.format("%s는 올바른 OAuth channel이 아닙니다", channel));
        }
    }

    public String getMainPageUrl() throws URISyntaxException {
        return new URI("https://mannatime.io").toString();
    }
}
