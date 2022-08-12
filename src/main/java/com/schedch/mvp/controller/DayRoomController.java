package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.room.DayRoomReq;
import com.schedch.mvp.dto.room.DayRoomRes;
import com.schedch.mvp.dto.room.DayRoomTopRes;
import com.schedch.mvp.mapper.DayRoomMapper;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.service.DayRoomService;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

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
        Room room = dayRoomMapper.req2Entity(dayRoomReq);
        String roomUuid = roomService.createRoom(room);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        log.info("created dayRoomUuid: {}, dates: {}", roomUuid, gson.toJson(dayRoomReq.getDates()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @GetMapping("/day/room/{roomUuid}")
    public ResponseEntity dayRoomFind(@PathVariable("roomUuid") String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        DayRoomRes dayRoomRes = dayRoomMapper.entity2Res(room);

        log.info("find dayRoomUuid: {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayRoomRes));
    }

    @GetMapping("day/room/{roomUuid}/top/{max}")
    public ResponseEntity dayRoomTopFind(@PathVariable("roomUuid") String roomUuid,
                                         @PathVariable("max") int max) {
        List<DayRoomTopRes> dayRoomTopResList = dayRoomService.getTopAvailableDate(roomUuid, max);

        log.info("find top list for roomUuid: {}, max: {}", roomUuid, max);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayRoomTopResList));

    }
}
