package com.schedch.mvp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.JwtConfig;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.config.oauth.OAuthConfigUtils;
import com.schedch.mvp.dto.sign.SignFromRoomReq;
import com.schedch.mvp.exception.CalendarLoadException;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.ParticipantService;
import com.schedch.mvp.service.oauth.OAuthService;
import com.schedch.mvp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.security.auth.login.FailedLoginException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SignController {

    private final OAuthConfigUtils oAuthConfigUtils;
    private final OAuthService oAuthService;
    private final UserService userService;
    private final ParticipantService participantService;
    private final JwtConfig jwtConfig;
    private final Gson gson;

    /**
     * path variable: channel - where user proceeded oauth
     * @return: response entity which contains url to continue oauth procedure
     */
    @PostMapping("/sign/in/{channel}")
    public ResponseEntity signIn(@PathVariable String channel,
                                 @Nullable @RequestBody SignFromRoomReq request) {
        log.info("P: signIn / channel = {}, request = {}", channel, request);

        String authUrl;
        if (request != null) {
            Long participantId = getParticipantId(request);
            String stateStr = "participantId " + participantId;
            authUrl = getSignAuthUrl(channel, stateStr);
        }
        else {
            authUrl = getSignAuthUrl(channel);
        }

        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("authUrl", authUrl);

        log.info("S: signIn / channel = {}", channel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(bodyJson));
    }

    @PostMapping("/sign/in/{channel}/calendar")
    public ResponseEntity signInGrantCalendar(@PathVariable String channel) {
        log.info("P: signInGrantCalendar / channel = {}", channel);

        String authUrl = getSignAuthUrl(channel, "calendarGrant");

        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("authUrl", authUrl);

        log.info("S: signInGrantCalendar / channel = {}", channel);
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
    @GetMapping(value = "/sign/in/redirect/google", params = {"code"})
    public ResponseEntity redirectGoogleSignIn(@RequestParam(value = "code") String authCode,
                                               @Nullable @RequestParam(value = "state") String encodedState) throws URISyntaxException, FailedLoginException, JsonProcessingException {

        log.info("P: redirectGoogleSignIn / authCode = {}", authCode);
        HttpHeaders headers = new HttpHeaders();

        try {
            User user = oAuthService.googleSignIn(authCode);

            if (encodedState != null) {
                String state = new String(Base64Utils.decodeFromUrlSafeString(encodedState));
                String[] split = state.split(" ");
                if(split[0].equals("participantId")) {
                    long participantId = Long.parseLong(split[1]);
                    log.info("P: redirectGoogleSignIn / add participant to user / userId = {}, participantId = {}", user.getId(), participantId);
                    participantService.addParticipantToUser(participantId, user);
                }

                if (split[0].equals("calendarGrant")) {
                    log.info("P: redirectGoogleSignIn / grantCalendar / userId = {}", user.getId());

                    String redirectUri = jwtConfig.getFrontCalendarGrantRedirect() + "?result=success";
                    headers.setLocation(new URI(redirectUri));
                    return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
                }
            }

            String accessToken = jwtConfig.createAccessTokenByUser(user);
            String refreshToken = jwtConfig.createRefreshToken();
            oAuthService.saveToken(user.getEmail(), accessToken, refreshToken);

            String uri = jwtConfig.getFrontSuccessRedirect()
                    + "?accessToken="
                    + accessToken;
            headers.setLocation(new URI(uri));

            return new ResponseEntity(headers, HttpStatus.SEE_OTHER); //redirect with accessToken

        } catch (CalendarLoadException e) {
            //TODO: error url로 변경
            headers.setLocation(new URI(oAuthConfigUtils.getFailurePageUrl()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        }

    }

    @GetMapping(value = "/sign/in/redirect/google", params = {"error"})
    public ResponseEntity googleSignInFailure(@RequestParam(value = "error") String error) throws URISyntaxException, FailedLoginException, JsonProcessingException {
        log.warn("F: googleSignInFailure / login failed / error = {}", error);
        HttpHeaders headers = new HttpHeaders();
        String redirectUri = jwtConfig.getFrontCalendarGrantRedirect() + "?result=fail";
        headers.setLocation(new URI(redirectUri));
        return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
    }

//    @GetMapping("/sign/in/redirect/kakao")
//    public ResponseEntity redirectKakaoSignIn(@RequestParam(value = "code") String authCode) throws URISyntaxException, JsonProcessingException, ParseException {
//        HttpHeaders headers = new HttpHeaders();
//
//        try {
//            User user = oAuthService.kakaoSignIn(authCode);
//            String accessToken = jwtConfig.createAccessTokenByUser(user);
//            String refreshToken = jwtConfig.createRefreshToken();
//            oAuthService.saveToken(user.getEmail(), accessToken, refreshToken);
//
//            String uri = jwtConfig.getFrontSuccessRedirect()
//                    + "?accessToken="
//                    + accessToken;
//            headers.setLocation(new URI(uri));
//
//            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
//
//        } catch (IllegalArgumentException e) {
//            //TODO: error url로 변경
//            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
//            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
//        }
//    }

    @DeleteMapping("/sign/out")
    public ResponseEntity signOut(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userEmail = principalDetails.getUsername();
        log.info("P: signOut / userEmail = {}", userEmail);

        userService.signOutUser(userEmail);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/sign/out/{channel}")
    public ResponseEntity signOut(@PathVariable String channel) {
        log.info("P: signOut / channel = {}", channel);
        String authUrl = oAuthConfigUtils.getSignOutAuthUrl(channel);
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("authUrl", authUrl);

        log.info("S: signOut / channel = {}", channel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(bodyJson));
    }

    @GetMapping("sign/out/redirect/google")
    public ResponseEntity googleSignOut(@RequestParam(value = "code") String authCode) throws URISyntaxException, FailedLoginException, JsonProcessingException {
        log.info("P: googleSignOut / authCode = {}", authCode);

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            String email = oAuthService.googleSignOut(authCode);

            oAuthService.deleteToken(email);

            log.info("S: googleSignOut / authCode = {}", authCode);
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);

        } catch (IllegalArgumentException e) { //this email is not signed in
            //TODO: error url로 변경
            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
        }
    }

//    @GetMapping("sign/out/redirect/kakao")
//    public ResponseEntity kakaoSignOut(@RequestParam(value = "code") String authCode) throws URISyntaxException, FailedLoginException, JsonProcessingException, ParseException {
//        HttpHeaders headers = new HttpHeaders();
//
//        try {
//            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
//            String email = oAuthService.kakaoSignOut(authCode);
//
//            oAuthService.deleteToken(email);
//
//            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
//
//        } catch (IllegalArgumentException e) {
//            //TODO: error url로 변경
//            headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));
//            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
//        }
//    }

    private String getSignAuthUrl(String channel) {
        return oAuthConfigUtils.getSignInAuthUrl(channel);
    }
    private String getSignAuthUrl(String channel, String state) {
        return oAuthConfigUtils.getAuthUrlWithState(channel, state);
    }

    private Long getParticipantId(SignFromRoomReq request) {
        String roomUuid = request.getRoomUuid();
        String participantName = request.getParticipantName();
        Long participantId = participantService.findParticipantIdByRoomAndName(roomUuid, participantName);
        return participantId;
    }
}
