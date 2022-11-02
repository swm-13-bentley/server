package com.schedch.mvp.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.config.oauth.OAuthConfigUtils;
import com.schedch.mvp.dto.user.UserCalendarListRes;
import com.schedch.mvp.dto.user.UserCalendarLoadPerDay;
import com.schedch.mvp.dto.user.UserCalendarReq;
import com.schedch.mvp.mapper.UserCalendarMapper;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.User;
import com.schedch.mvp.model.UserCalendar;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.user.UserCalendarService;
import com.schedch.mvp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.FailedLoginException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserCalendarController {

    private final UserService userService;
    private final UserCalendarService userCalendarService;
    private final RoomService roomService;
    private final OAuthConfigUtils oAuthConfigUtils;
    private final UserCalendarMapper userCalendarMapper;
    private final Gson gson;

    @PostMapping("/user/calendar")
    public ResponseEntity userAddCalendar(@AuthenticationPrincipal PrincipalDetails principalDetails) throws URISyntaxException {
        String userEmail = principalDetails.getUsername();
        User user = userService.getUserByEmail(userEmail);
        log.info("P: userAddCalendar / userId = {}", user.getId());

        String redirectUri = oAuthConfigUtils.getUserCalendarAddAuthUrl(user.getId(), "google");
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(redirectUri));

        log.info("S: userAddCalendar() / userId = {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(redirectUri);
    }

    @GetMapping("/calendar/redirect/google")
    public ResponseEntity getAddCalendarRedirect(@RequestParam(value = "code") String authCode,
                                                 @RequestParam(value = "state") Long userId) throws FailedLoginException, JsonProcessingException, URISyntaxException {
        log.info("P: getAddCalendarRedirect() / authCode received for userId = {}", userId);
        userCalendarService.addCalendar(userId, authCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));

        log.info("S: getAddCalendarRedirect() / userId = {}", userId);
        return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/user/calendar")
    public ResponseEntity loadUserCalendarEvents(@RequestParam(value = "roomUuid") String roomUuid,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: loadUserCalendarEvents() / userId = {}", user.getId());

        String userEmail = principalDetails.getUsername();
        Room room = roomService.getRoomWithRoomDates(roomUuid);

        try {
            UserCalendarLoadPerDay response = userCalendarService.loadCalendarEvents(userEmail, room);
            log.info("S: loadUserCalendarEvents() / userId = {}, roomUuid = {}", user.getId(), roomUuid);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(gson.toJson(response));

        } catch (NoSuchElementException e) {
            log.warn("E: loadUserCalendarEvents() / No main calendar / userId = {}", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalAccessException e) {
            log.warn("E: loadCalendarEvents / calendar access is not granted / userId = {}", user.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/user/calendar/all")
    public ResponseEntity findAllUserCalendar(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: findAllUserCalendar() / userId = {}", user.getId());

        String userEmail = principalDetails.getUsername();
        List<UserCalendar> userCalendarList = userCalendarService.getAllUserCalendar(userEmail);

        UserCalendarListRes response = new UserCalendarListRes();
        userCalendarList.stream().forEach(userCalendar -> {
                    response.getCalendarList().add(userCalendarMapper.userCalendar2Res(userCalendar));
                    if (userCalendar.isMainCalendar()) {
                        response.setMainCalendarEmail(userCalendar.getCalendarEmail());
                        response.setMainCalendarChannel(userCalendar.getCalendarChannel());
                    }
                }
        );
        log.info("S: findAllUserCalendar() / userId = {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(response));
    }

    @DeleteMapping("/user/calendar")
    public ResponseEntity deleteUserCalendar(@RequestParam("calendarId") Long calendarId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: findAllUserCalendar / userId = {}", user.getId());

        try {
            userCalendarService.deleteUserCalendar(user.getId(), calendarId);

            log.info("S: findAllUserCalendar / userId = {}", user.getId());
            return ResponseEntity.status(HttpStatus.OK)
                    .build();

        } catch (IllegalArgumentException e) { //user does not own userCalendar, userCalendarId does not exist in db
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) { //calendar is main calendar
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

    }

    @PatchMapping("/user/calendar/main")
    public ResponseEntity patchMainCalendar(@RequestParam("newMainCalendarId") Long newMainCalendarId,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: patchMainCalendar / userId = {}", user.getId());

        String userEmail = principalDetails.getUsername();
        try {
            userCalendarService.changeMainUserCalendar(userEmail, newMainCalendarId);

            log.info("S: patchMainCalendar / userId = {}, newMainCalendarId = {}", user.getId(), newMainCalendarId);
            return ResponseEntity.status(HttpStatus.OK)
                    .build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/user/calendar/selected")
    public ResponseEntity changeSelectedSubCalendar(@RequestBody UserCalendarReq userCalendarReq,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: changeSelectedSubCalendar / userId = {}", user.getId());
        String userEmail = principalDetails.getUsername();

        try {
            userCalendarService.changeSelectedSubCalendar(userEmail, userCalendarReq);

            log.info("S: changeSelectedSubCalendar / userId = {}", user.getId());
            return ResponseEntity.status(HttpStatus.OK)
                    .build();

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

}