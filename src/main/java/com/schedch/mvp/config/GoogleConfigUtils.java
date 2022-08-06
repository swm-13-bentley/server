package com.schedch.mvp.config;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Getter
@PropertySource("classpath:google.yaml")
public class GoogleConfigUtils {
    @Value("${google.auth.url}")
    private String googleAuthUrl;

    @Value("${google.access.type}")
    private String googleAccessType;

    @Value("${google.login.url}")
    private String googleLoginUrl;

    @Value("${google.redirect.uri}")
    private String googleRedirectUrl;

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

    private JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Google 로그인 URL 생성 로직
    public String googleInitUrl(String state) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_type", getGoogleAccessType());
        params.put("client_id", getGoogleClientId());
        params.put("redirect_uri", getGoogleRedirectUrl());
        params.put("response_type", "code");
        params.put("scope", getScopeUrl());
        params.put("state", state);

        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return getGoogleLoginUrl()
                + "/o/oauth2/v2/auth"
                + "?"
                + paramStr;
    }

    public String getScopeUrl() {
        return scopes.replaceAll(",", "%20");
    }

    public List<String> getScopeList() {
        return Arrays.stream(scopes.split(",")).collect(Collectors.toList());
    }
}
