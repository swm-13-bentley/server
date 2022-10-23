package com.schedch.mvp.controller.user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.dto.participant.DayParticipantRes;
import com.schedch.mvp.dto.participant.ParticipantRes;
import com.schedch.mvp.dto.room.DayGroupSeperateRes;
import com.schedch.mvp.dto.room.DayRoomReq;
import com.schedch.mvp.dto.room.GroupSeperateRes;
import com.schedch.mvp.dto.room.RoomRequest;
import com.schedch.mvp.dto.user.UserParticipatingRoomRes;
import com.schedch.mvp.exception.UserNotInRoomException;
import com.schedch.mvp.mapper.DayRoomMapper;
import com.schedch.mvp.mapper.RoomMapper;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.user.UserRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRoomController {

    private final Gson gson;
    private final UserRoomService userRoomService;
    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final DayRoomMapper dayRoomMapper;

    @PostMapping("/user/room")
    public ResponseEntity createPremiumRoom(@Valid @RequestBody RoomRequest roomReq) {
        log.info("P: createPremiumRoom / roomReq = {}", gson.toJson(roomReq));

        Room room = roomMapper.req2Entity(roomReq);
        String roomUuid = roomService.createPremiumRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("S: createPremiumRoom / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @PostMapping("/user/day/room")
    public ResponseEntity createPremiumDayRoom(@Valid @RequestBody DayRoomReq dayRoomReq) {
        log.info("P: createPremiumDayRoom / dayRoomReq = {}", gson.toJson(dayRoomReq));

        Room room = dayRoomMapper.req2Entity(dayRoomReq);
        String roomUuid = roomService.createPremiumRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("S: createPremiumDayRoom / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @PostMapping("/user/room/{roomUuid}/entry")
    public ResponseEntity userRoomEntry(@PathVariable String roomUuid, @AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException{
        User user = principalDetails.getUser();
        log.info("P: userRoomEntry / userId = {}, roomUuid = {}", user.getId(), roomUuid);

        String userEmail = getUserEmail(principalDetails);
        Participant participant = userRoomService.entry(userEmail, roomUuid);
        ParticipantRes participantRes = new ParticipantRes(participant);

        log.info("S: userRoomEntry / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(participantRes));
    }

    @PostMapping("/user/day/room/{roomUuid}/entry")
    public ResponseEntity userDayRoomEntry(@PathVariable String roomUuid, @AuthenticationPrincipal PrincipalDetails principalDetails) throws IllegalAccessException{
        User user = principalDetails.getUser();
        log.info("P: userDayRoomEntry / userId = {}, roomUuid = {}", user.getId(), roomUuid);

        String userEmail = getUserEmail(principalDetails);
        Participant participant = userRoomService.entry(userEmail, roomUuid);
        DayParticipantRes dayParticipantRes = new DayParticipantRes(participant);

        log.info("S: userRoomEntry / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayParticipantRes));
    }

    @PostMapping("/user/room/{roomUuid}/exit")
    public ResponseEntity userRoomExit(@PathVariable String roomUuid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: userRoomExit / userId = {}, roomUuid = {}", user.getId(), roomUuid);

        String userEmail = getUserEmail(principalDetails);
        try {
            userRoomService.exitRoom(userEmail, roomUuid);

            log.info("S: userRoomExit / userId = {}, roomUuid = {}", user.getId(), roomUuid);
            return ResponseEntity.status(HttpStatus.OK)
                    .build();

        } catch (UserNotInRoomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("user/room/{roomUuid}/group/seperate")
    public ResponseEntity roomGroupWithoutUser(@PathVariable String roomUuid,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("P: roomGroupWithoutUser / roomUuid = {}", roomUuid);

        String userEmail = principalDetails.getUsername();
        Long participantId = userRoomService.getParticipantIdInRoom(userEmail, roomUuid);
        List<Participant> participantList = roomService.getAllParticipantSchedules(roomUuid);
        GroupSeperateRes groupSeperateRes = new GroupSeperateRes(participantList, participantId);

        log.info("S: roomGroupWithoutUser / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(groupSeperateRes));
    }

    @GetMapping("user/day/room/{roomUuid}/group/seperate")
    public ResponseEntity dayRoomGroupWithoutUser(@PathVariable("roomUuid") String roomUuid,
                                                  @AuthenticationPrincipal PrincipalDetails principalDetails)  {
        log.info("P: dayRoomGroupWithoutUser / roomUuid = {}", roomUuid);

        String userEmail = principalDetails.getUsername();
        Long participantId = userRoomService.getParticipantIdInRoom(userEmail, roomUuid);
        List<Participant> participantList = roomService.getAllParticipantSchedules(roomUuid);
        DayGroupSeperateRes dayGroupSeperateRes = new DayGroupSeperateRes(participantList, participantId);

        log.info("S: dayRoomGroupWithoutUser / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayGroupSeperateRes));
    }

    @GetMapping("/user/myRoom/unConfirmed")
    public ResponseEntity getAllUnConfirmedRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: getAllUnConfirmedRooms / userId = {}", user.getId());

        String userEmail = getUserEmail(principalDetails);
        List<UserParticipatingRoomRes> resList = userRoomService.getAllRooms(userEmail, false);

        log.info("S: getAllUnConfirmedRooms / userId = {}", user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(resList));
    }

    @GetMapping("/user/myRoom/confirmed")
    public ResponseEntity getAllConfirmedRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        log.info("P: getAllConfirmedRooms / userId = {}", user.getId());

        String userEmail = getUserEmail(principalDetails);
        List<UserParticipatingRoomRes> resList = userRoomService.getAllRooms(userEmail, true);

        log.info("S: getAllConfirmedRooms / userId = {}", user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(resList));
    }

    private String getUserEmail(PrincipalDetails principalDetails) {
        String userEmail = principalDetails.getUsername();
        return userEmail;
    }
}
