package com.schedch.mvp.service.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.schedch.mvp.config.oauth.KakaoConfigUtils;
import com.schedch.mvp.dto.oauth.KakaoProfileRes;
import com.schedch.mvp.dto.oauth.KakaoTokenRes;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoProfileService {

    private final KakaoConfigUtils kakaoConfigUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public KakaoProfileRes getKakaoProfileRes(String authCode, boolean isSignIn) throws JsonProcessingException, ParseException {
        String accessToken = getKakaoToken(authCode, isSignIn);
        KakaoProfileRes kakaoProfileRes = getUserProfile(accessToken);

        return kakaoProfileRes;
    }

    public String getKakaoToken(String authCode, boolean isSignIn) throws JsonProcessingException {
        String redirectUri = isSignIn ?
                kakaoConfigUtils.getSignInRedirectUri() : kakaoConfigUtils.getSignOutRedirectUri();

        //used multiValueMap in order to pass params by x-www-form-urlencoded form
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("client_id", kakaoConfigUtils.getClientId());
        requestParams.add("redirectUri", redirectUri);
        requestParams.add("code", authCode);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap> httpRequestEntity = new HttpEntity<>(requestParams, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(kakaoConfigUtils.getTokenUri(), httpRequestEntity, String.class);

        ObjectMapper objectMapper = getObjectMapper();
        KakaoTokenRes kakaoTokenRes = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<KakaoTokenRes>() {});

        return kakaoTokenRes.getAccessToken();
    }

    public KakaoProfileRes getUserProfile(String accessToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("property_keys", kakaoConfigUtils.getScopeJson());

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap> httpRequestEntity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(kakaoConfigUtils.getProfileUri(), httpRequestEntity, String.class);

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(apiResponseJson.getBody());
        JSONObject kakao_account = (JSONObject) obj.get("kakao_account");
        JSONObject profile = (JSONObject) kakao_account.get("profile");

        String id = obj.get("id").toString();
        String nickname = profile.get("nickname").toString();
        String email = kakao_account.get("email").toString();
        String password = bCryptPasswordEncoder.encode("defaultPwd");
        return new KakaoProfileRes(id, nickname, email, password);

    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
