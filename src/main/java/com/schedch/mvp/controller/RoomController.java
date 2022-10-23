package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.participant.ParticipantRes;
import com.schedch.mvp.dto.TopCountRes;
import com.schedch.mvp.dto.room.GroupSeperateRes;
import com.schedch.mvp.dto.room.RoomRequest;
import com.schedch.mvp.dto.room.RoomResponse;
import com.schedch.mvp.mapper.RoomMapper;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.TopTime;
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
public class RoomController {

    private final RoomService roomService;
    private final Gson gson;
    private final RoomMapper roomMapper;

    @PostMapping("/room")
    public ResponseEntity createRoom(@Valid @RequestBody RoomRequest roomReq) {
        log.info("P: createRoom / roomReq = {}", gson.toJson(roomReq));
        Room room = roomMapper.req2Entity(roomReq);
        String roomUuid = roomService.createRoom(room);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("S: createRoom / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @GetMapping("/room/{roomUuid}")
    public ResponseEntity getRoomInfo(@PathVariable("roomUuid") String roomUuid) {
        log.info("P: getRoomInfo / roomUuid = {}", roomUuid);
        Room room = roomService.getRoomWithParticipants(roomUuid);
        RoomResponse roomResponse = roomMapper.entity2Res(room);

        log.info("S: getRoomInfo / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(roomResponse));
    }

    @GetMapping("/room/{roomUuid}/top/{max}")
    public ResponseEntity getTopTimes(@PathVariable("roomUuid") String roomUuid,
                                      @PathVariable("max") int max) {
        log.info("P: getTopTimes / roomUuid = {}, max = {}", roomUuid, max);

        List<TopTime> topAvailableTimeAndNames = roomService.getTopAvailableTimeAndNames(roomUuid, max);
        List<TopCountRes> responseList = topAvailableTimeAndNames.stream().map(timeCount -> {
            return TopCountRes.builder()
                    .count(timeCount.getParticipantSize())
                    .availableDate(timeCount.getAvailableDate())
                    .startTime(TimeAdapter.startBlock2Str(timeCount.getStart()))
                    .endTime(TimeAdapter.endBlock2Str(timeCount.getStart() + timeCount.getLen() - 1))
                    .participants(timeCount.getParticipantNames())
                    .build();
        }).collect(Collectors.toList());

        log.info("roomUuid = {}, max = {}, responseList = {}", roomUuid, max, gson.toJson(responseList));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(responseList));
    }


    @GetMapping("/room/{roomUuid}/group")
    public ResponseEntity groupSchedulesFind(@PathVariable String roomUuid) {
        log.info("P: groupSchedulesFind / roomUuid = {}", roomUuid);

        List<Participant> participants = roomService.getAllParticipantSchedules(roomUuid);
        List<ParticipantRes> response = participants.stream().map(ParticipantRes::new).collect(Collectors.toList());

        log.info("S: groupSchedulesFind / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(response));
    }

    @GetMapping("/room/{roomUuid}/group/seperate/{participantName}")
    public ResponseEntity roomGroupFindSeperateParticipant(@PathVariable String roomUuid,
                                                           @PathVariable String participantName) {
        log.info("P: roomGroupFindSeperateParticipant / roomUuid = {}", roomUuid);

        List<Participant> participantList = roomService.getAllParticipantSchedules(roomUuid);
        GroupSeperateRes groupSeperateRes = new GroupSeperateRes(participantList, participantName);


        log.info("S: roomGroupFindSeperateParticipant / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(groupSeperateRes));
    }

}
