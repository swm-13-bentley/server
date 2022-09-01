package com.schedch.mvp.service.google;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.schedch.mvp.config.GoogleConfigUtils;
import com.schedch.mvp.dto.GoogleLoginRequest;
import com.schedch.mvp.dto.GoogleLoginResponse;
import com.schedch.mvp.dto.google.GoogleLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final GoogleConfigUtils googleConfigUtils;

    public GoogleLoginDto getGoogleUserProfile(String authCode) throws Exception {
        ObjectMapper objectMapper = getObjectMapper();
        GoogleLoginResponse googleLoginResponse = getGoogleLoginResponseByAuthCode(objectMapper, authCode);
        GoogleLoginDto googleLoginDto = getGoogleLoginDto(objectMapper, googleLoginResponse);

        googleLoginDto.setAccessToken(googleLoginResponse.getAccessToken());
        googleLoginDto.setExpiresIn(googleLoginDto.getExpiresIn());
        googleLoginDto.setRefreshToken(googleLoginDto.getRefreshToken());
        googleLoginDto.setScope(googleLoginDto.getScope());

        return googleLoginDto;
    }

    private GoogleLoginDto getGoogleLoginDto(ObjectMapper objectMapper, GoogleLoginResponse googleLoginResponse) throws Exception {
        String jwtToken = googleLoginResponse.getIdToken();
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleConfigUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();
        String resultJson = new RestTemplate().getForObject(requestUrl, String.class);

        if(resultJson != null) {
            GoogleLoginDto googleLoginDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});
            return googleLoginDto;
        }
        else {
            throw new Exception("Google OAuth failed!");
        }
    }

    public GoogleLoginResponse getGoogleLoginResponseByAuthCode(ObjectMapper objectMapper, String code) {
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(googleConfigUtils.getGoogleClientId())
                .clientSecret(googleConfigUtils.getGoogleSecret())
                .code(code)
                .redirectUri(googleConfigUtils.getGoogleRedirectUrl())
                .grantType("authorization_code")
                .build();

        try {
            // Http Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
            ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfigUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

            // ObjectMapper를 통해 String to Object로 변환
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

            return googleLoginResponse;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
