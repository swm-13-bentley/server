package com.schedch.mvp.service.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.schedch.mvp.config.oauth.GoogleConfigUtils;
import com.schedch.mvp.dto.GoogleLoginRequest;
import com.schedch.mvp.dto.GoogleLoginResponse;
import com.schedch.mvp.dto.oauth.GoogleLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.login.FailedLoginException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleProfileService {

    private final GoogleConfigUtils googleConfigUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public GoogleLoginDto getGoogleProfile(String authCode, boolean isSignIn) throws FailedLoginException, JsonProcessingException {
        log.info("P: getGoogleProfile / authCode = {}", authCode);
        ObjectMapper objectMapper = getObjectMapper();
        GoogleLoginResponse googleLoginResponse = getGoogleLoginResponseByAuthCode(objectMapper, authCode, isSignIn);
        GoogleLoginDto googleLoginDto = getGoogleLoginDto(objectMapper, googleLoginResponse);

        log.info("S: getGoogleProfile / authCode = {}", authCode);
        return googleLoginDto;
    }

    public GoogleLoginDto getGoogleProfileByType(String authCode, String type) throws FailedLoginException, JsonProcessingException {
        log.info("P: getGoogleProfileByType / authCode = {}", authCode);

        ObjectMapper objectMapper = getObjectMapper();
        GoogleLoginResponse googleLoginResponse = getGoogleLoginResponseByAuthCodeByType(objectMapper, authCode, type);
        GoogleLoginDto googleLoginDto = getGoogleLoginDto(objectMapper, googleLoginResponse);

        log.info("S: getGoogleProfileByType / authCode = {}", authCode);
        return googleLoginDto;
    }

    public GoogleLoginResponse getGoogleLoginResponseByAuthCode(ObjectMapper objectMapper, String authCode, boolean isSignIn) throws JsonProcessingException {
        log.info("P: getGoogleLoginResponseByAuthCode / authCode = {}", authCode);

        String redirectUri = isSignIn ?
                googleConfigUtils.getGoogleSignInRedirectUrl() : googleConfigUtils.getGoogleSignOutRedirectUrl();

        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(googleConfigUtils.getGoogleClientId())
                .clientSecret(googleConfigUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();

        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfigUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

        GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

        log.info("S: getGoogleLoginResponseByAuthCode / authCode = {}, scope = {}", authCode, googleLoginResponse.getScope());
        return googleLoginResponse;

    }

    public GoogleLoginResponse getGoogleLoginResponseByAuthCodeByType(ObjectMapper objectMapper, String authCode, String type) throws JsonProcessingException {
        log.info("P: getGoogleLoginResponseByAuthCodeByType / authCode = {}", authCode);

        String redirectUri = "";
        switch (type) {
            case "addCalendar":
                redirectUri = googleConfigUtils.getGoogleAddCalendarRedirectUrl();
                break;
        }

        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(googleConfigUtils.getGoogleClientId())
                .clientSecret(googleConfigUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();

        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfigUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

        GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

        log.info("S: getGoogleLoginResponseByAuthCodeByType / authCode = {}", authCode);
        return googleLoginResponse;

    }

    private GoogleLoginDto getGoogleLoginDto(ObjectMapper objectMapper, GoogleLoginResponse googleLoginResponse) throws FailedLoginException, JsonProcessingException {
        log.info("P: getGoogleLoginDto");

        String jwtToken = googleLoginResponse.getIdToken();
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleConfigUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();
        String resultJson = new RestTemplate().getForObject(requestUrl, String.class);

        if(resultJson != null) {
            GoogleLoginDto googleLoginDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});
            googleLoginDto.setAccessToken(googleLoginResponse.getAccessToken());
            googleLoginDto.setExpiresIn(googleLoginResponse.getExpiresIn());
            googleLoginDto.setRefreshToken(googleLoginResponse.getRefreshToken());
            googleLoginDto.setScope(googleLoginResponse.getScope());
            googleLoginDto.setPassword(bCryptPasswordEncoder.encode("defaultPwd"));
            return googleLoginDto;
        }
        else {
            log.error("F: getGoogleLoginDto / google resultJson is null");
            throw new FailedLoginException("Google OAuth failed!");
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
