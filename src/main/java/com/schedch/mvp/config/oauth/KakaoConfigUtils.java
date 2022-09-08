package com.schedch.mvp.config.oauth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Getter
@PropertySource("classpath:oauth.yaml")
public class KakaoConfigUtils {

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.sign.in.redirect.uri}")
    private String signInRedirectUri;

    @Value("${kakao.sign.out.redirect.uri}")
    private String signOutRedirectUri;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.token.uri}")
    private String tokenUri;

    @Value("${kakao.profile.uri}")
    private String profileUri;

    @Value("${kakao.scopes}")
    private String scopes;

    public String kakaoSignInitUrl(boolean isSignIn) {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", getClientId());
        if(isSignIn) {
            params.put("redirect_uri", getSignInRedirectUri());
        } else {
            params.put("redirect_uri", getSignOutRedirectUri());
        }

        return paramsToInitUrl(params);
    }

    private String paramsToInitUrl(Map<String, Object> params) {
        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return "https://kauth.kakao.com/oauth/authorize"
                + "?"
                + paramStr;
    }

    public String getScopeJson() {
        List<String> scopeList = Arrays.asList(getScopes().split(","));
        return new Gson().toJson(scopeList);
    }

}
