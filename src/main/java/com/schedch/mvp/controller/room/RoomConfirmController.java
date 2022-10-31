package com.schedch.mvp.controller.room;

import com.google.gson.Gson;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.room.RoomConfirmReq;
import com.schedch.mvp.dto.room.RoomInRangeRes;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.service.room.RoomConfirmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomConfirmController {

    private final RoomConfirmService roomConfirmService;
    private final Gson gson;

    @GetMapping("/room/{roomUuid}/confirm")
    public ResponseEntity getParticipantInRange(@PathVariable String roomUuid,
                                                @RequestParam("availableDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availableDate,
                                                @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime startTime,
                                                @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime endTime) {

        log.info("P: getParticipantInRange / roomUuid = {}, availableDate = {}, startTime = {}, endTime = {}", roomUuid, availableDate, startTime, endTime);

        Map<String, List> participantMap = roomConfirmService.findInRangeParticipantList(roomUuid, availableDate, startTime, endTime);
        List<Participant> inRange = participantMap.get("inRange");
        List<Participant> notInRange = participantMap.get("notInRange");

        RoomInRangeRes roomInRangeRes = new RoomInRangeRes(inRange, notInRange);

        log.info("S: getParticipantInRange / inRangeSize = {}, notInRangeSize = {}", inRange.size(), notInRange.size());
        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(roomInRangeRes));

    }

    @PatchMapping("/room/{roomUuid}/confirm")
    public ResponseEntity patchRoomConfirm(@PathVariable String roomUuid,
                                           @Valid @RequestBody RoomConfirmReq roomConfirmReq) {
        log.info("P: patchRoomConfirm / roomUuid = {}", roomUuid);

        LocalDate confirmedDate = roomConfirmReq.getConfirmedDate();
        LocalTime startTime = TimeAdapter.str2LocalTime(roomConfirmReq.getStartTime());
        LocalTime endTime;
        if(roomConfirmReq.getEndTime().equals("24:00:00")) {
            endTime = LocalTime.of(23, 59, 0);
        } else {
            endTime = TimeAdapter.str2LocalTime(roomConfirmReq.getEndTime());
        }

        //participantIdList is null in RoomConfirmAllSendImplementation implementation
        roomConfirmService.confirmRoom(roomUuid, confirmedDate, startTime, endTime, null);

        log.info("S: patchRoomConfirm / roomUuid = {}", roomUuid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
