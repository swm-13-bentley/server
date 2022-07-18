package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.ParticipantRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Participant", description = "Participant(참가자) 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    @Operation(summary = "비회원 방 입장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "200 OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "401 Unauthorized: 잘못된 비밀번호"),
            @ApiResponse(responseCode = "404", description = "404 Not Found: 존재하지 않는 roomUuid")
    })
    @PostMapping("/room/{roomUuid}/participant/entry")
    public ResponseEntity participantFind(@Parameter(description = "접속하려는 방의 UUID", required = true, example = "e894e0ef-b6b2-4d03-a4d8-32b3aead7976")
                                              @PathVariable String roomUuid,
                                          @RequestBody ParticipantRequestDto participantRequestDto) {
        String participantName = participantRequestDto.getParticipantName();
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

    @Operation(summary = "비회원 가능한 시간 입력")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "200 OK"),
            @ApiResponse(responseCode = "404", description = "404 Not Found: 존재하지 않는 roomUuid")
    })
    @PostMapping("/room/{roomUuid}/participant/available")
    public ResponseEntity participantAvailablePost(@Parameter(description = "접속하려는 방의 UUID", required = true, example = "e894e0ef-b6b2-4d03-a4d8-32b3aead7976")
                                                       @PathVariable String roomUuid,
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

    @Operation(summary = "방에 속한 모든 인원의 일정 가져오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "200 OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ParticipantResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "404 Not Found: 존재하지 않는 roomUuid")
    })
    @GetMapping("/room/{roomUuid}/group")
    public ResponseEntity groupSchedulesFind(@Parameter(description = "접속하려는 방의 UUID", required = true, example = "e894e0ef-b6b2-4d03-a4d8-32b3aead7976")
                                                @PathVariable String roomUuid) {
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
