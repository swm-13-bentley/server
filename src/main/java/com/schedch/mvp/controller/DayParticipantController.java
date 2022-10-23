package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.dto.participant.ParticipantReq;
import com.schedch.mvp.dto.participant.DayParticipantReq;
import com.schedch.mvp.dto.participant.DayParticipantRes;
import com.schedch.mvp.mapper.DayParticipantMapper;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DayParticipantController {

    private final ParticipantService participantService;
    private final DayParticipantMapper dayParticipantMapper;
    private final Gson gson;

    @PostMapping("day/room/{roomUuid}/participant/entry")
    public ResponseEntity dayParticipantFind(@PathVariable("roomUuid") String roomUuid,
                                             @RequestBody ParticipantReq participantReq) throws IllegalAccessException {
        String participantName = participantReq.getParticipantName();
        String password = participantReq.getPassword();
        log.info("P: dayParticipantFind / roomUuid = {}, participantName = {}", roomUuid, participantName);

        Participant participant = participantService.getParticipant(roomUuid, participantName, password);
        DayParticipantRes dayParticipantRes = dayParticipantMapper.entity2Res(participant);

        log.info("S: dayParticipantFind / roomUuid = {}, participantName = {}", roomUuid, participantName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayParticipantRes));
    }

    @PostMapping("day/room/{roomUuid}/participant/available")
    public ResponseEntity saveDayParticipantAvailable(@PathVariable("roomUuid") String roomUuid,
                                                      @Valid @RequestBody DayParticipantReq dayParticipantReq) throws IllegalAccessException{
        String participantName = dayParticipantReq.getParticipantName();
        log.info("P: saveDayParticipantAvailable / roomUuid = {}, participantName = {}, dayParticipantReq = {}", roomUuid, participantName, gson.toJson(dayParticipantReq));

        List<LocalDate> availableDates = dayParticipantReq.getAvailableDates();
        participantService.saveDayParticipantAvailable(roomUuid, participantName, availableDates);

        log.info("S: saveDayParticipantAvailable / roomUuid = {}, participantName = {}", roomUuid, participantName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
