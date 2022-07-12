package com.schedch.mvp.controller;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomRequestDto roomRequestDto) {
        String roomUuid = roomService.createRoom(roomRequestDto);
        return ResponseEntity.ok().body("roomUuid");
    }

//    @GetMapping("/room/{roomUuid}")
//    public String getRoomInfo(@PathVariable("roomUuid") String roomUuid) {
//        roomService
//    }
}
