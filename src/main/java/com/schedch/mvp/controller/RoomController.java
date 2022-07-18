package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.dto.RoomResponseDto;
import com.schedch.mvp.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import javax.validation.Valid;
import java.util.NoSuchElementException;

@Tag(name = "Room", description = "Room(방) 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "200 OK: 성공시 roomUuid를 반환", content = @Content(mediaType = "application/json", schema = @Schema(type = "json", example = "{\"roomUuid\": \"e894e0ef-b6b2-4d03-a4d8-32b3aead7976\"}"))),
            @ApiResponse(responseCode = "400", description = "400 BAD REQUEST: Input 중 누락된 값이 있음")
    })
    @PostMapping("/room")
    public ResponseEntity createRoom(@Valid @RequestBody RoomRequestDto roomRequestDto) {
        String roomUuid = roomService.createRoom(roomRequestDto);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomUuid", roomUuid);

        return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(jsonObject));
    }

    @Operation(summary = "방 정보 가져오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "200 OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "404 Not Found: 해당 UUID에 대한 방이 없음")
    })
    @GetMapping("/room/{roomUuid}")
    public ResponseEntity getRoomInfo(@Parameter(description = "접속하려는 방의 UUID", required = true, example = "e894e0ef-b6b2-4d03-a4d8-32b3aead7976")
                                          @PathVariable("roomUuid") String roomUuid) {
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
