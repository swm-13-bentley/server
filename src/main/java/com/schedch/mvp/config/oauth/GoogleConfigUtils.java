package com.schedch.mvp.config.oauth;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Getter
@PropertySource("classpath:oauth.yaml")
public class GoogleConfigUtils {
    @Value("${google.auth.url}")
    private String googleAuthUrl;

    @Value("${google.access.type}")
    private String googleAccessType;

    @Value("${google.login.url}")
    private String googleLoginUrl;

    @Value("${google.sign.in.redirect.uri}")
    private String googleSignInRedirectUrl;

    @Value("${google.sign.out.redirect.uri}")
    private String googleSignOutRedirectUrl;

    @Value("${google.add.calendar.redirect.uri}")
    private String googleAddCalendarRedirectUrl;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.secret}")
    private String googleSecret;

    @Value("${google.auth.scope}")
    private String scopes;

    @Value("${google.auth.CREDENTIALS_FILE_PATH}")
    private String CREDENTIALS_FILE_PATH;

    @Value("${google.auth.TOKENS_DIRECTORY_PATH}")
    private String TOKENS_DIRECTORY_PATH;

    @Value("${google.front.location}")
    private String frontPath;

    private JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Google 로그인 URL 생성 로직
    public String googleSignInInitUrl() {
        Map<String, Object> params = getParamsMap();
        params.put("redirect_uri", getGoogleSignInRedirectUrl());
        params.put("scope", getScopeUrl());

        return getOAuthUrl(params);
    }

    public String googleSignInInitUrl(String stateString) {
        Map<String, Object> params = getParamsMap();
        params.put("redirect_uri", getGoogleSignInRedirectUrl());
        params.put("scope", getScopeUrl());

        params.put("state", Base64Utils.encodeToUrlSafeString(stateString.getBytes()));

        return getOAuthUrl(params);
    }

    public String googleSignOutInitUrl(String state) {
        Map<String, Object> params = getParamsMap();
        params.put("redirect_uri", getGoogleSignOutRedirectUrl());
        params.put("scope", getScopeUrl());

        if(state != null) {//add state if not null
            params.put("state", state);
        }

        return getOAuthUrl(params);
    }

    public String googleAddCalendarInitUrl(Long userId) {
        Map<String, Object> params = getParamsMap();
        params.put("redirect_uri", getGoogleAddCalendarRedirectUrl());
        params.put("scope", getScopeUrl());
        params.put("state", userId.toString());

        return getOAuthUrl(params);
    }
    public String getScopeUrl() {
        return scopes.replaceAll(",", "%20");
    }

    public List<String> getScopeList() {
        return Arrays.stream(scopes.split(",")).collect(Collectors.toList());
    }

    private Map<String, Object> getParamsMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_type", getGoogleAccessType());
        params.put("client_id", getGoogleClientId());
        params.put("response_type", "code");

        return params;
    }

    private String getOAuthUrl(Map<String, Object> params) {
        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return getGoogleLoginUrl()
                + "/o/oauth2/v2/auth"
                + "?"
                + paramStr;
    }
}
