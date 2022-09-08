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
public class GoogleProfileService {

    private final GoogleConfigUtils googleConfigUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public GoogleLoginDto getGoogleProfile(String authCode, boolean isSignIn) throws FailedLoginException, JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();
        GoogleLoginResponse googleLoginResponse = getGoogleLoginResponseByAuthCode(objectMapper, authCode, isSignIn);
        GoogleLoginDto googleLoginDto = getGoogleLoginDto(objectMapper, googleLoginResponse);

        return googleLoginDto;
    }

    public GoogleLoginResponse getGoogleLoginResponseByAuthCode(ObjectMapper objectMapper, String code, boolean isSignIn) throws JsonProcessingException {
        String redirectUri = isSignIn ?
                googleConfigUtils.getGoogleSignInRedirectUrl() : googleConfigUtils.getGoogleSignOutRedirectUrl();

        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(googleConfigUtils.getGoogleClientId())
                .clientSecret(googleConfigUtils.getGoogleSecret())
                .code(code)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();

        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfigUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

        GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

        return googleLoginResponse;

    }

    private GoogleLoginDto getGoogleLoginDto(ObjectMapper objectMapper, GoogleLoginResponse googleLoginResponse) throws FailedLoginException, JsonProcessingException {
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
