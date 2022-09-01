package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.OAuthConfigUtils;
import com.schedch.mvp.dto.google.GoogleLoginDto;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.UserService;
import com.schedch.mvp.service.google.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SignInController {

    private final UserService userService;
    private final OAuthConfigUtils oAuthConfigUtils;
    private final GoogleOAuthService googleOAuthService;
    private final Gson gson;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * path variable: channel - where user proceeded oauth
     * @return: response entity which contains url to continue oauth procedure
     */
    @GetMapping("/sign/in/{channel}")
    public ResponseEntity signIn(@PathVariable String channel) {
        String authUrl = oAuthConfigUtils.getAuthUrl(channel);
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
    public ResponseEntity redirectGoogleLogin(@RequestParam(value = "code") String authCode) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));

        try {
            GoogleLoginDto profile = googleOAuthService.getGoogleUserProfile(authCode);
            User user = User.builder()
                    .username(profile.getName())
                    .password(bCryptPasswordEncoder.encode("defaultpwd"))
                    .email(profile.getEmail())
                    .signInChannel("google")
                    .calendarChannel("google")
                    .calendarAccessToken(profile.getAccessToken())
                    .calendarRefreshToken(profile.getRefreshToken())
                    .build();

            System.out.println(gson.toJson(user));
            userService.save(user);
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson(e.getMessage()));
        }
    }
}
