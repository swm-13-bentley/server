package com.schedch.mvp.controller.user;

import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.dto.user.UserAvailableDayReq;
import com.schedch.mvp.dto.user.UserAvailableTimeReq;
import com.schedch.mvp.exception.UserNotInRoomException;
import com.schedch.mvp.service.user.UserParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserParticipantController {

    private final UserParticipantService userParticipantService;

    @PostMapping("/user/room/{roomUuid}/participant/available")
    public ResponseEntity saveUserAvailableTime(@PathVariable String roomUuid,
                                        @RequestBody UserAvailableTimeReq userAvailableTimeReq,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        String userEmail = getUserEmail(principalDetails);
        userParticipantService.saveAvailableTimeToRoom(userEmail, roomUuid, userAvailableTimeReq);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/user/day/room/{roomUuid}/participant/available")
    public ResponseEntity saveUserAvailableDay(@PathVariable String roomUuid,
                                               @RequestBody UserAvailableDayReq userAvailableDayReq,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        String userEmail = getUserEmail(principalDetails);
        userParticipantService.saveAvailableDayToRoom(userEmail, roomUuid, userAvailableDayReq);
        return ResponseEntity.status(HttpStatus.OK)
                .build();

    }

    @PostMapping("/user/room/{roomUuid}/name/{changedName}")
    public ResponseEntity changeParticipantName(@PathVariable String roomUuid,
                                                @PathVariable String changedName,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        String userEmail = getUserEmail(principalDetails);
        userParticipantService.changeParticipantName(userEmail, roomUuid, changedName);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/user/room/{roomUuid}/title/{changedTitle}")
    public ResponseEntity changeRoomTitle(@PathVariable String roomUuid,
                                                @PathVariable String changedTitle,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) throws UserNotInRoomException {
        String userEmail = getUserEmail(principalDetails);
        userParticipantService.changeRoomTitle(userEmail, roomUuid, changedTitle);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    private String getUserEmail(PrincipalDetails principalDetails) {
        String userEmail = principalDetails.getUsername();
        return userEmail;
    }
}
