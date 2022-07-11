package com.schedch.mvp.controller;

import com.schedch.mvp.dto.RoomRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    @PostMapping("/room")
    public String createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        return "success";
    }
}
