package com.schedch.mvp.service.room;

import com.schedch.mvp.dto.email.EmailReq;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.email.AwsMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomConfirmService {

    private final AwsMailService awsMailService;
    private final RoomService roomService;
    private final ParticipantRepository participantRepository;

    public Map<String, List> findInRangeParticipantList(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime) {
        List<Participant> participantList = roomService.getAllParticipantSchedules(roomUuid);

        ArrayList<Participant> notInRangeParticipantList = new ArrayList<>();
        ArrayList<Participant> inRangeParticipantList = new ArrayList<>();

        //참가자를 순회하면서 해당 날짜 및 시간에 가능한 사람 찾기
        for (Participant p : participantList) {
            boolean isInRange;
            if (startTime != null && endTime != null) {
                isInRange = p.findInTimeRangeSchedule(confirmedDate, startTime, endTime);
            } else {
                isInRange = p.findInDayRangeSchedule(confirmedDate);
            }

            if(isInRange) {
                inRangeParticipantList.add(p);
            }
            else {
                notInRangeParticipantList.add(p);
            }
        }

        HashMap<String, List> returnMap = new HashMap<>();
        returnMap.put("inRange", inRangeParticipantList);
        returnMap.put("notInRange", notInRangeParticipantList);

        return returnMap;
    }

    public void confirmRoom(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Long> participantIdList) {
        log.info("P: confirmRoom / roomUuid = {}", roomUuid);

        //확정 인원에 포함할 모든 참가자를 불러온다
        List<Participant> participantList = participantRepository.findAllByIdIn(participantIdList);

        //방 상태를 confirm으로 전환
        Room room = roomService.getRoom(roomUuid);
        room.setConfirmed(true);

        //참가자들 중 이메일을 등록한 사람들에게 메일을 발송한다
        List<Participant> emailRegisteredParticipantList = participantList.stream().filter(participant -> participant.getAlarmEmail() != null).collect(Collectors.toList());
        sendEmail(roomUuid, confirmedDate, startTime, endTime, emailRegisteredParticipantList);

        log.info("S: confirmRoom / roomUuid = {}", roomUuid);
    }

    private void sendEmail(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Participant> participantList) {
        log.info("P: sendEmail / roomUuid = {}", roomUuid);
        EmailReq emailReq;
        if (startTime == null || endTime == null) {
            emailReq = getDayEmailReq(roomUuid, confirmedDate);
        }
        else {
            emailReq = getTimeEmailReq(roomUuid, confirmedDate, startTime, endTime);
        }

        participantList.stream().forEach(p -> {
            setEmailReqInfo(emailReq, p);
            awsMailService.sendEmailBySes(emailReq);
        });
        log.info("S: sendEmail / roomUuid = {}", roomUuid);
    }

    private EmailReq getTimeEmailReq(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime) {
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

    private EmailReq getDayEmailReq(String roomUuid, LocalDate confirmedDate) {
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

    private void setEmailReqInfo(EmailReq emailReq, Participant participant) {
        emailReq.setEmailTitle("언제만나에서 약속이 확정되었습니다: " + participant.getRoomTitle());
        emailReq.setMailTo(participant.getAlarmEmail());
        emailReq.setAttendeeName(participant.getParticipantName());
        emailReq.setRoomTitle(participant.getRoomTitle());
        emailReq.setSummary(participant.getRoomTitle());
    }
}
