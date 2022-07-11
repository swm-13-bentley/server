package com.schedch.mvp.controller;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;
    @PostMapping("/room")
    public String createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        String roomUuid = roomService.createRoom(roomRequestDto);
        return roomUuid;
    }
}
