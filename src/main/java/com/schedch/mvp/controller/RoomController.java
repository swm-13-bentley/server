package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.TopCountRes;
import com.schedch.mvp.dto.room.RoomRequest;
import com.schedch.mvp.dto.room.RoomResponse;
import com.schedch.mvp.mapper.RoomMapper;
import com.schedch.mvp.model.Room;
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
    private final TimeAdapter timeAdapter;

    @PostMapping("/room")
    public ResponseEntity createRoom(@Valid @RequestBody RoomRequest roomReq) {
        Room room = roomMapper.req2Entity(roomReq);
        String roomUuid = roomService.createRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("created roomUuid: {}, roomInfo: {}", roomUuid, gson.toJson(roomReq));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @GetMapping("/room/{roomUuid}")
    public ResponseEntity getRoomInfo(@PathVariable("roomUuid") String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        RoomResponse roomResponse = roomMapper.entity2Res(room);

        log.info("roomUuid: {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(roomResponse));
    }

    @GetMapping("/room/{roomUuid}/top/{max}")
    public ResponseEntity getTopTimes(@PathVariable("roomUuid") String roomUuid,
                                      @PathVariable("max") int max) {
        List<RoomService.TopTime> topAvailableTimeAndNames = roomService.getTopAvailableTimeAndNames(roomUuid, max);
        List<TopCountRes> responseList = topAvailableTimeAndNames.stream().map(timeCount -> {
            return TopCountRes.builder()
                    .size(timeCount.getParticipantSize())
                    .availableDate(timeCount.getAvailableDate())
                    .startTime(timeAdapter.startBlock2Str(timeCount.getStart()))
                    .endTime(timeAdapter.endBlock2Str(timeCount.getStart() + timeCount.getLen() - 1))
                    .build();
        }).collect(Collectors.toList());

        log.info("roomUuid: {}, max: {}, foundLen: {}", roomUuid, max, responseList.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(responseList));
    }

}
