package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.RoomRequest;
import com.schedch.mvp.dto.RoomResponse;
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

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    private final Gson gson;
    private final RoomMapper roomMapper;

    @PostMapping("/room")
    public ResponseEntity createRoom(@Valid @RequestBody RoomRequest roomReq) {
        Room room = roomMapper.req2Entity(roomReq);
        String roomUuid = roomService.createRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("created roomUuid: {}", roomUuid);
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
        log.info("roomUuid: {}, max: {}", roomUuid, max);
        List<RoomService.TimeCount> topAvailableTime = roomService.getTopAvailableTime(roomUuid, max);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(topAvailableTime));
    }
}
