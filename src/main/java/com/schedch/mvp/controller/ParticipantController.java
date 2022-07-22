package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.ParticipantRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;
    private final Gson gson;

    @PostMapping("/room/{roomUuid}/participant/entry")
    public ResponseEntity participantFind(@PathVariable String roomUuid,
                                          @RequestBody ParticipantRequestDto participantRequestDto) throws IllegalAccessException {
        String participantName = participantRequestDto.getParticipantName();
        String password = participantRequestDto.getPassword();

        ParticipantResponseDto participantResponseDto
                = participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password);

        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(participantResponseDto));

    }

    @PostMapping("/room/{roomUuid}/participant/available")
    public ResponseEntity participantAvailablePost(@PathVariable String roomUuid,
                                                   @RequestBody AvailableRequestDto availableRequestDto) {
        participantService.saveParticipantAvailable(roomUuid, availableRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/room/{roomUuid}/group")
    public ResponseEntity groupSchedulesFind(@PathVariable String roomUuid) {
        List<ParticipantResponseDto> participantResponseDtoList = participantService.findAllParticipantsInRoom(roomUuid);
        return ResponseEntity.status(HttpStatus.OK).body(participantResponseDtoList);
    }

}
