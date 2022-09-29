package com.schedch.mvp.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.util.DateTime;
import com.google.gson.Gson;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.config.oauth.OAuthConfigUtils;
import com.schedch.mvp.dto.user.UserCalendarListRes;
import com.schedch.mvp.dto.user.UserCalendarLoadRes;
import com.schedch.mvp.dto.user.UserCalendarReq;
import com.schedch.mvp.mapper.UserCalendarMapper;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.User;
import com.schedch.mvp.model.UserCalendar;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.user.UserCalendarService;
import com.schedch.mvp.service.user.UserService;
import lombok.RequiredArgsConstructor;
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

        String redirectUri = oAuthConfigUtils.getUserCalendarAddAuthUrl(user.getId(), "google");
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(redirectUri));

        return ResponseEntity.status(HttpStatus.OK)
                .body(redirectUri);
//        return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/calendar/redirect/google")
    public ResponseEntity getAddCalendarRedirect(@RequestParam(value = "code") String authCode,
                                                 @RequestParam(value = "state") Long userId) throws FailedLoginException, JsonProcessingException, URISyntaxException {

        userCalendarService.addCalendar(userId, authCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(oAuthConfigUtils.getMainPageUrl()));

        return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/user/calendar")
    public ResponseEntity loadUserCalendarEvents(@RequestParam(value = "roomUuid") String roomUuid,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {

        String userEmail = principalDetails.getUsername();
        Room room = roomService.getRoomWithRoomDates(roomUuid);

        DateTime startDateTime = TimeAdapter.localDateAndTime2DateTime(room.getStartLocalDate(), room.getStartTime(), "+9");
        DateTime endDateTime = TimeAdapter.localDateAndTime2DateTime(room.getEndLocalDate(), room.getEndTime(), "+9");

        try {
            List<UserCalendarLoadRes> response = userCalendarService.loadCalendarEvents(userEmail, startDateTime, endDateTime);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(gson.toJson(response));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/user/calendar/all")
    public ResponseEntity findAllUserCalendar(@AuthenticationPrincipal PrincipalDetails principalDetails) {

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

        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(response));
    }

    @DeleteMapping("/user/calendar")
    public ResponseEntity deleteUserCalendar(@RequestParam("calendarId") Long calendarId) {
        try {
            userCalendarService.deleteUserCalendar(calendarId);
            return ResponseEntity.status(HttpStatus.OK)
                    .build();

        } catch (IllegalArgumentException e) { //no such calendar exists for this user
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
        String userEmail = principalDetails.getUsername();

        try {
            userCalendarService.changeMainUserCalendar(userEmail, newMainCalendarId);
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
        String userEmail = principalDetails.getUsername();

        try {
            userCalendarService.changeSelectedSubCalendar(userEmail, userCalendarReq);
            return ResponseEntity.status(HttpStatus.OK)
                    .build();

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

}