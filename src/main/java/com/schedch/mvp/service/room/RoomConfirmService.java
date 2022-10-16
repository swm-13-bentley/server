package com.schedch.mvp.service.room;

import com.schedch.mvp.dto.email.EmailReq;
import com.schedch.mvp.model.Participant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RoomConfirmService {

    abstract Map<String, List> findInRangeParticipantList(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime);

    abstract void confirmRoom(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Long> participantIdList);

    abstract void sendEmail(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Participant> participantList);

    default EmailReq getTimeEmailReq(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime) {
        return EmailReq.builder()
                .dateOnly(false)
                .startDateTime(LocalDateTime.of(confirmedDate, startTime))
                .endDateTime(LocalDateTime.of(confirmedDate, endTime))
                .roomLink("https://mannatime.io/ko/entry/" + roomUuid)
                .sequence("0")
                .uid(UUID.randomUUID().toString())
                .location("미정")
                .build();
    }

    default EmailReq getDayEmailReq(String roomUuid, LocalDate confirmedDate) {
        return EmailReq.builder()
                .dateOnly(true)
                .startDateTime(LocalDateTime.of(confirmedDate, LocalTime.of(0, 0, 0)))
                .endDateTime(LocalDateTime.of(confirmedDate.plusDays(1), LocalTime.of(0, 0, 0)))
                .roomLink("https://mannatime.io/ko/entry/" + roomUuid)
                .sequence("0")
                .uid(UUID.randomUUID().toString())
                .location("미정")
                .build();
    }

    default void setEmailReqInfo(EmailReq emailReq, Participant participant) {
        emailReq.setEmailTitle("언제만나에서 약속이 확정되었습니다: " + participant.getRoomTitle());
        emailReq.setMailTo(participant.getAlarmEmail());
        emailReq.setAttendeeName(participant.getParticipantName());
        emailReq.setRoomTitle(participant.getRoomTitle());
        emailReq.setSummary(participant.getRoomTitle());
    }
}
