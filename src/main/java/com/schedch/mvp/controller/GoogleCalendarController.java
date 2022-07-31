package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.config.GoogleConfigUtils;
import com.schedch.mvp.dto.CalendarResponse;
import com.schedch.mvp.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;
    private final GoogleConfigUtils googleConfigUtils;
    private final Gson gson;


//    @GetMapping("/room/{roomUuid}/participant/google/{code}")
    @GetMapping("/room/{roomUuid}/participant/google")
    public ResponseEntity unSignedUserCalendarFind(@PathVariable("roomUuid") String roomUuid,
//                                                    @PathVariable("code") String code,
                                                   @RequestParam(name = "code") String code) {
        try {
            List<CalendarResponse> schedulesInRoomRange = googleCalendarService.getSchedulesInRoomRange(roomUuid, code);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(schedulesInRoomRange));
        } catch (Exception e) {
            System.out.println("failure");
            System.out.println("e.getMessage() = " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(e.getMessage()));
        }

    }

    @GetMapping("/google/login")
    public ResponseEntity<Object> moveGoogleInitUrl() {
        String authUrl = googleConfigUtils.googleInitUrl();
        System.out.println("authUrl = " + authUrl);
        URI redirectUri = null;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/google/login/redirect")
    public String redirectGoogleLogin(@RequestParam(value = "code") String authCode) throws GeneralSecurityException, IOException {
        String testRoomUuid = "d3b558c4-11d2-4707-9669-505a1b5a283d";
        List<CalendarResponse> calendarResponseList = redirectTest(testRoomUuid, authCode);

        return gson.toJson(calendarResponseList);
    }

    public List<CalendarResponse> redirectTest(String roomUuid, String code) throws GeneralSecurityException, IOException {
        System.out.println("unSignedUserCalendarFind 호출됨");
        List<CalendarResponse> schedulesInRoomRange = googleCalendarService.getSchedulesInRoomRange(roomUuid, code);
        return schedulesInRoomRange;
    }
}
