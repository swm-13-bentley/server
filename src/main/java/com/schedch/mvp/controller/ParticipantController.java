package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.ParticipantRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/room/{roomUuid}/participant/available")
    public ResponseEntity participantFind(@PathVariable String roomUuid,
                                          @RequestBody ParticipantRequestDto participantRequestDto) {
        String participantName = participantRequestDto.getParticipantName();
        String password = participantRequestDto.getPassword();
        Gson gson = new Gson();

        try {
            ParticipantResponseDto participantResponseDto
                    = participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password);
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty(participantName, participantResponseDto.toString());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(gson.toJson(responseJsonObject));

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
}
