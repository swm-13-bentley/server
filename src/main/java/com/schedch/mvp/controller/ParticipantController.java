package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.participant.ParticipantReq;
import com.schedch.mvp.dto.participant.ParticipantRes;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.dto.participant.ParticipantAlarmEmailReq;
import com.schedch.mvp.exception.FullMemberException;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;
    private final Gson gson;

    @PostMapping("/room/{roomUuid}/participant/entry")
    public ResponseEntity participantFind(@PathVariable String roomUuid,
                                          @RequestBody ParticipantReq participantReq) throws IllegalAccessException {
        String participantName = participantReq.getParticipantName();
        String password = participantReq.getPassword();

        log.info("P: participantFind / roomUuid = {}, participantName = {}", roomUuid, participantName);
        try {
            Participant participant = participantService.getParticipant(roomUuid, participantName, password);
            ParticipantRes participantRes = new ParticipantRes(participant);

            log.info("S: participantFind / roomUuid = {}, participantName = {}", roomUuid, participantName);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(gson.toJson(participantRes));

        } catch (FullMemberException e) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(gson.toJson(errorJson));
        }

    }

    @PostMapping("/room/{roomUuid}/participant/available")
    public ResponseEntity participantAvailablePost(@PathVariable String roomUuid,
                                                   @RequestBody AvailableRequestDto availableRequestDto) {
        log.info("P: participantAvailablePost / roomUuid = {}, availableRequestDto = {}", roomUuid, gson.toJson(availableRequestDto));

        String participantName = availableRequestDto.getParticipantName();
        List<TimeBlockDto> available = availableRequestDto.getAvailable();
        participantService.saveParticipantAvailable(roomUuid, participantName, available);

        log.info("S: participantAvailablePost / roomUuid = {}, participantName = {}", roomUuid, participantName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PatchMapping("/room/{roomUuid}/participant/alarmEmail")
    public ResponseEntity patchParticipantAlarmEmail(@PathVariable String roomUuid,
                                                     @Valid @RequestBody ParticipantAlarmEmailReq participantAlarmEmailReq) {
        log.info("P: patchParticipantAlarmEmail / roomUuid = {}", roomUuid);

        String participantName = participantAlarmEmailReq.getParticipantName();
        String alarmEmail = participantAlarmEmailReq.getAlarmEmail();
        participantService.registerAlarmEmail(roomUuid, participantName, alarmEmail);

        log.info("S: patchParticipantAlarmEmail / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
