package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.dto.RoomResponseDto;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    private final Gson gson;

    @PostMapping("/room")
    public ResponseEntity createRoom(@Valid @RequestBody RoomRequestDto roomRequestDto) {
        String roomUuid = roomService.createRoom(roomRequestDto);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(jsonObject));
    }

    @GetMapping("/room/{roomUuid}")
    public ResponseEntity getRoomInfo(@PathVariable("roomUuid") String roomUuid) {
        RoomResponseDto roomResponseDto = roomService.getRoomInfo(roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomResponseDto);
    }
}함
