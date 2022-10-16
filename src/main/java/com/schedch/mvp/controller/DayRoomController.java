package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.participant.DayParticipantRes;
import com.schedch.mvp.dto.room.DayGroupSeperateRes;
import com.schedch.mvp.dto.room.DayRoomReq;
import com.schedch.mvp.dto.room.DayRoomRes;
import com.schedch.mvp.dto.room.DayRoomTopRes;
import com.schedch.mvp.mapper.DayRoomMapper;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.service.DayRoomService;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DayRoomController {

    private final DayRoomMapper dayRoomMapper;
    private final RoomService roomService;
    private final DayRoomService dayRoomService;
    private final Gson gson;

    @PostMapping("/day/room")
    public ResponseEntity dayRoomCreate(@Valid @RequestBody DayRoomReq dayRoomReq) {
        log.info("P: dayRoomCreate / dayRoomReq = {}", dayRoomReq);

        Room room = dayRoomMapper.req2Entity(dayRoomReq);
        String roomUuid = roomService.createRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("S: dayRoomCreate / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @GetMapping("/day/room/{roomUuid}")
    public ResponseEntity dayRoomFind(@PathVariable("roomUuid") String roomUuid) {
        log.info("P: dayRoomCreate / roomUuid = {}", roomUuid);

        Room room = roomService.getRoomWithParticipants(roomUuid);
        DayRoomRes dayRoomRes = dayRoomMapper.entity2Res(room);

        log.info("S: dayRoomCreate / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayRoomRes));
    }

    @GetMapping("day/room/{roomUuid}/top/{max}")
    public ResponseEntity dayRoomTopFind(@PathVariable("roomUuid") String roomUuid,
                                         @PathVariable("max") int max) {
        log.info("P: dayRoomTopFind / roomUuid = {}, max = {}", roomUuid, max);

        List<DayRoomTopRes> dayRoomTopResList = dayRoomService.getTopAvailableDate(roomUuid, max);

        log.info("S: dayRoomTopFind / dayRoomTopResList.size() = {}", dayRoomTopResList.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayRoomTopResList));

    }

    @GetMapping("day/room/{roomUuid}/group")
    public ResponseEntity dayRoomGroup(@PathVariable("roomUuid") String roomUuid) {
        log.info("P: dayRoomGroup / roomUuid = {}", roomUuid);

        List<Participant> participants = roomService.getAllParticipantSchedules(roomUuid);
        List<DayParticipantRes> response = participants.stream().map(DayParticipantRes::new).collect(Collectors.toList());

        log.info("S: dayRoomGroup / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(response));
    }

    @GetMapping("day/room/{roomUuid}/group/seperate/{participantName}")
    public ResponseEntity dayRoomGroupSeperateParticipant(@PathVariable("roomUuid") String roomUuid,
                                                          @PathVariable String participantName) {
        log.info("P: dayRoomGroupSeperateParticipant / roomUuid = {}", roomUuid);

        List<Participant> participantList = roomService.getAllParticipantSchedules(roomUuid);
        DayGroupSeperateRes dayGroupSeperateRes = new DayGroupSeperateRes(participantList, participantName);

        log.info("S: dayRoomGroupSeperateParticipant / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayGroupSeperateRes));
    }
}
