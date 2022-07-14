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
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    @PostMapping("/room")
    public ResponseEntity createRoom(@Valid @RequestBody RoomRequestDto roomRequestDto) {
        String roomUuid = roomService.createRoom(roomRequestDto);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(jsonObject));
    }

    @GetMapping("/room/{roomUuid}")
    public ResponseEntity getRoomInfo(@PathVariable("roomUuid") String roomUuid) {
        try {
            RoomResponseDto roomResponseDto = roomService.getRoomInfo(roomUuid);
            return ResponseEntity.ok().body(roomResponseDto);
        } catch (NoSuchElementException e) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Gson().toJson(errorJson));
        }
    }
}
