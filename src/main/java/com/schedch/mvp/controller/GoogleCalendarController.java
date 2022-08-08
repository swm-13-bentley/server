package com.schedch.mvp.controller;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.GoogleConfigUtils;
import com.schedch.mvp.dto.CalendarResponse;
import com.schedch.mvp.model.GToken;
import com.schedch.mvp.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;
    private final GoogleConfigUtils googleConfigUtils;
    private final Gson gson;

    @PostMapping("google/calendar")
    public ResponseEntity googleCalendarTokenRequest() {
        String state = UUID.randomUUID().toString();
        String authUrl = googleConfigUtils.googleInitUrl(state);
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("state", state);
        bodyJson.addProperty("authUrl", authUrl);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(bodyJson));
    }

    @GetMapping("google/calendar")
    public List<CalendarResponse> googleCalendarLoad(@RequestParam(value = "roomUuid") String roomUuid,
                                                     @RequestParam(value = "state") String state) throws GeneralSecurityException, IOException {
        List<CalendarResponse> schedulesInRoomRange = googleCalendarService.getSchedulesInRoomRange(roomUuid, state);
        return schedulesInRoomRange;
    }

    @GetMapping("/google/calendar/redirect")
    public ResponseEntity redirectGoogleLogin(@RequestParam(value = "code") String authCode,
                                      @RequestParam(value = "state") String state) {
        TokenResponse tokenResponse = googleCalendarService.getTokenResponse(authCode);
        GToken gToken = GToken.builder()
                .state(state)
                .accessToken(tokenResponse.getAccessToken())
                .expiresIn(tokenResponse.getExpiresInSeconds())
                .refreshToken(tokenResponse.getRefreshToken())
                .scope(tokenResponse.getScope())
                .tokenType(tokenResponse.getTokenType())
                .build();
        googleCalendarService.save(gToken);

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(googleConfigUtils.getFrontPath()));
            return new ResponseEntity(headers, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
