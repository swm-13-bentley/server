package com.schedch.mvp.controller.user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.dto.room.DayRoomReq;
import com.schedch.mvp.dto.room.RoomRequest;
import com.schedch.mvp.dto.user.UserParticipatingRoomRes;
import com.schedch.mvp.mapper.DayRoomMapper;
import com.schedch.mvp.mapper.RoomMapper;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
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
        Room room = roomMapper.req2Entity(roomReq);
        String roomUuid = roomService.createPremiumRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("created PREMIUM roomUuid: {}, roomInfo: {}", roomUuid, gson.toJson(roomReq));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @PostMapping("/user/day/room")
    public ResponseEntity createPremiumDayRoom(@Valid @RequestBody DayRoomReq dayRoomReq) {
        Room room = dayRoomMapper.req2Entity(dayRoomReq);
        String roomUuid = roomService.createPremiumRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("created dayRoomUuid: {}, dates: {}", roomUuid, gson.toJson(dayRoomReq.getDates()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @PostMapping("/user/room/{roomUuid}/entry")
    public ResponseEntity userRoomEntry(@PathVariable String roomUuid, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        ParticipantResponseDto resDto = userRoomService.entry(user.getEmail(), roomUuid);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(resDto));
    }

    @GetMapping("/user/myRoom/unConfirmed")
    public ResponseEntity getAllUnConfirmedRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<UserParticipatingRoomRes> resList = userRoomService.getAllRooms(principalDetails.getUser().getEmail(), false);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(resList));
    }

    @GetMapping("/user/myRoom/confirmed")
    public ResponseEntity getAllConfirmedRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<UserParticipatingRoomRes> resList = userRoomService.getAllRooms(principalDetails.getUser().getEmail(), true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(resList));
    }
}
