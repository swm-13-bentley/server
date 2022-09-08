package com.schedch.mvp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.JwtConfig;
import com.schedch.mvp.config.oauth.OAuthConfigUtils;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.oauth.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.FailedLoginException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SignController {

    private final OAuthConfigUtils oAuthConfigUtils;
    private final OAuthService oAuthService;
    private final JwtConfig jwtConfig;
    private final Gson gson;

    /**
     * path variable: channel - where user proceeded oauth
     * @return: response entity which contains url to continue oauth procedure
     */
    @PostMapping("/sign/in/{channel}")
    public ResponseEntity signIn(@PathVariable String channel) {
        String authUrl = oAuthConfigUtils.getSignInAuthUrl(channel);
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("authUrl", authUrl);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(bodyJson));
    }

    /**
     * handle redirect - receives auth code
     * @param authCode - received auth code from OAuth server
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/sign/in/redirect/google")
    @ResponseBody
    public ResponseEntity redirectGoogleSignIn(@RequestParam(value = "code") String authCode) throws URISyntaxException, FailedLoginException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            User user = oAuthService.googleSignIn(authCode);
            String accessToken = jwtConfig.createAccessTokenByUser(user);
            String refreshToken = jwtConfig.createRefreshToken();
            oAuthService.saveToken(user.getEmail(), accessToken, refreshToken);

            redirectAccessToken(accessToken);

            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        } catch (IllegalArgumentException e) { //이미 회원 가입된 이메일인 경우
            //TODO: error url로 변경
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
        }
    }

    @GetMapping("/sign/in/redirect/kakao")
    public ResponseEntity redirectKakaoSignIn(@RequestParam(value = "code") String authCode) throws URISyntaxException, JsonProcessingException, ParseException {
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            User user = oAuthService.kakaoSignIn(authCode);
            String accessToken = jwtConfig.createAccessTokenByUser(user);
            String refreshToken = jwtConfig.createRefreshToken();
            oAuthService.saveToken(user.getEmail(), accessToken, refreshToken);

            redirectAccessToken(accessToken);

            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        } catch (IllegalArgumentException e) {
            //TODO: error url로 변경
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
        }
    }

    /**
     * in order to sign out, user must log in first (no matter of jwt token)
     *
     * @param channel
     * @return
     */
    @PostMapping("/sign/out/{channel}")
    public ResponseEntity signOut(@PathVariable String channel) {
        String authUrl = oAuthConfigUtils.getSignOutAuthUrl(channel);
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("authUrl", authUrl);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(bodyJson));
    }

    @GetMapping("sign/out/redirect/google")
    public ResponseEntity googleSignOut(@RequestParam(value = "code") String authCode) throws URISyntaxException, FailedLoginException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            String email = oAuthService.googleSignOut(authCode);

            oAuthService.deleteToken(email);

            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        } catch (IllegalArgumentException e) {
            //TODO: error url로 변경
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
        }
    }

    @GetMapping("sign/out/redirect/kakao")
    public ResponseEntity kakaoSignOut(@RequestParam(value = "code") String authCode) throws URISyntaxException, FailedLoginException, JsonProcessingException, ParseException {
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            String email = oAuthService.kakaoSignOut(authCode);

            oAuthService.deleteToken(email);

            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        } catch (IllegalArgumentException e) {
            //TODO: error url로 변경
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
        }
    }

    public void redirectAccessToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = jwtConfig.getFrontSuccessRedirect()
                + "?access-token="
                + accessToken;
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        res.getStatusCode();
    }
}
