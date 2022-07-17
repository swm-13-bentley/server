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
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/room/{roomUuid}/participant/entry")
    public ResponseEntity participantFind(@PathVariable String roomUuid,
                                          @RequestBody ParticipantRequestDto participantRequestDto) {
        String participantName = participantRequestDto.getUsername();
        String password = participantRequestDto.getPassword();
        Gson gson = new Gson();

        try {
            ParticipantResponseDto participantResponseDto
                    = participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(gson.toJson(participantResponseDto));

        } catch (IllegalAccessException e1) {//이름 중복
            JsonObject errorJsonObject = new JsonObject();
            errorJsonObject.addProperty("message", e1.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(gson.toJson(errorJsonObject));

        } catch (NoSuchElementException e2) {//해당 roomUuid에 대한 방 없음
            JsonObject errorJsonObject = new JsonObject();
            errorJsonObject.addProperty("message", e2.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(gson.toJson(errorJsonObject));
        }
    }

    @PostMapping("/room/{roomUuid}/participant/available")
    public ResponseEntity participantAvailablePost(@PathVariable String roomUuid,
                                                   @RequestBody AvailableRequestDto availableRequestDto) {
        Gson gson = new Gson();

        try {
            participantService.saveParticipantAvailable(roomUuid, availableRequestDto);

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (NoSuchElementException e) {//해당 roomUuid에 대한 방 없음
            JsonObject errorJsonObject = new JsonObject();
            errorJsonObject.addProperty("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(gson.toJson(errorJsonObject));
        }
    }

    @GetMapping("/room/{roomUuid}/group")
    public ResponseEntity groupSchedulesFind(@PathVariable String roomUuid) {
        Gson gson = new Gson();

        try {
            List<ParticipantResponseDto> participantResponseDtoList = participantService.findAllParticipantsInRoom(roomUuid);
            return ResponseEntity.status(HttpStatus.OK).body(participantResponseDtoList);
        }
        catch (NoSuchElementException e) {
            JsonObject errorJsonObject = new JsonObject();
            errorJsonObject.addProperty("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(gson.toJson(errorJsonObject));
        }
    }

}
