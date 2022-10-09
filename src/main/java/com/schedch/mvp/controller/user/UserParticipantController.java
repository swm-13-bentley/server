package com.schedch.mvp.controller.user;

import com.google.gson.Gson;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.dto.user.UserAvailableDayReq;
import com.schedch.mvp.dto.user.UserAvailableTimeReq;
import com.schedch.mvp.dto.user.UserEmailReq;
import com.schedch.mvp.exception.UserNotInRoomException;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.user.UserParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserParticipantController {

    private final UserParticipantService userParticipantService;
    private final Gson gson;

    @PostMapping("/user/room/{roomUuid}/participant/available")
    public ResponseEntity saveUserAvailableTime(@PathVariable String roomUuid,
                                        @RequestBody UserAvailableTimeReq userAvailableTimeReq,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        User user = principalDetails.getUser();
        log.info("P: saveUserAvailableTime / userId = {}, userAvailableTimeReq = {}", user.getId(), gson.toJson(userAvailableTimeReq));

        String userEmail = getUserEmail(principalDetails);
        userParticipantService.saveAvailableTimeToRoom(userEmail, roomUuid, userAvailableTimeReq);

        log.info("S: saveUserAvailableTime / userId = {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/user/day/room/{roomUuid}/participant/available")
    public ResponseEntity saveUserAvailableDay(@PathVariable String roomUuid,
                                               @RequestBody UserAvailableDayReq userAvailableDayReq,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        User user = principalDetails.getUser();
        log.info("P: saveUserAvailableDay / userId = {}, userAvailableDayReq = {}", user.getId(), gson.toJson(userAvailableDayReq));

        String userEmail = getUserEmail(principalDetails);
        userParticipantService.saveAvailableDayToRoom(userEmail, roomUuid, userAvailableDayReq);

        log.info("S: saveUserAvailableDay / userId = {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();

    }

    @PostMapping("/user/room/{roomUuid}/name/{changedName}")
    public ResponseEntity changeParticipantName(@PathVariable String roomUuid,
                                                @PathVariable String changedName,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        User user = principalDetails.getUser();
        log.info("P: changeParticipantName / userId = {}, roomUuid = {}, changedName = {}", user.getId(), roomUuid, changedName);

        String userEmail = getUserEmail(principalDetails);
        userParticipantService.changeParticipantName(userEmail, roomUuid, changedName);

        log.info("S: changeParticipantName / userId = {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/user/room/{roomUuid}/title/{changedTitle}")
    public ResponseEntity changeRoomTitle(@PathVariable String roomUuid,
                                                @PathVariable String changedTitle,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        User user = principalDetails.getUser();
        log.info("P: changeRoomTitle / userId = {}, changedTitle = {}", user.getId(), changedTitle);

        String userEmail = getUserEmail(principalDetails);
        userParticipantService.changeRoomTitle(userEmail, roomUuid, changedTitle);

        log.info("S: changeRoomTitle / userId = {}, changedTitle = {}", user.getId(), changedTitle);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/user/room/{roomUuid}/alarmEmail")
    public ResponseEntity userAlarmEmailPatch(@PathVariable String roomUuid,
                                              @RequestBody UserEmailReq userEmailReq,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: userAlarmEmailPatch / userId = {}", user.getId());

        String userEmail = getUserEmail(principalDetails);
        String alarmEmail = userEmailReq.getAlarmEmail();
        userParticipantService.registerAlarmEmail(userEmail, roomUuid, alarmEmail);

        log.info("S: userAlarmEmailPatch / userId = {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    private String getUserEmail(PrincipalDetails principalDetails) {
        String userEmail = principalDetails.getUsername();
        return userEmail;
    }
}
