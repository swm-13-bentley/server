package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.dto.ParticipantRequestDto;
import com.schedch.mvp.dto.participant.DayParticipantReq;
import com.schedch.mvp.dto.participant.DayParticipantRes;
import com.schedch.mvp.mapper.DayParticipantMapper;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.service.DayParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DayParticipantController {

    private final DayParticipantService dayParticipantService;
    private final DayParticipantMapper dayParticipantMapper;
    private final Gson gson;

    @PostMapping("day/room/{roomUuid}/participant/load")
    public ResponseEntity dayParticipantFind(@PathVariable("roomUuid") String roomUuid,
                                             @RequestBody ParticipantRequestDto participantRequestDto) throws IllegalAccessException {
        String participantName = participantRequestDto.getParticipantName();
        String password = participantRequestDto.getPassword();

        Participant participant = dayParticipantService.findParticipant(roomUuid, participantName, password);
        DayParticipantRes dayParticipantRes = dayParticipantMapper.entity2Res(participant);

        log.info("roomUuid: {}, pName: {}, pwd: {}", roomUuid, participantName, password);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson(dayParticipantRes));
    }

    @PostMapping("day/room/{roomUuid}/participant/available")
    public ResponseEntity saveDayParticipantAvailable(@PathVariable("roomUuid") String roomUuid,
                                                      @Valid @RequestBody DayParticipantReq dayParticipantReq) throws IllegalAccessException{

        String participantName = dayParticipantReq.getParticipantName();
        String password = dayParticipantReq.getPassword();
        List<LocalDate> availableDates = dayParticipantReq.getAvailableDates();
        dayParticipantService.saveParticipantAvailable(roomUuid, participantName, password, availableDates);

        log.info("roomUuid: {}, participantName: {}, dates: {}", roomUuid, participantName, gson.toJson(availableDates));
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
