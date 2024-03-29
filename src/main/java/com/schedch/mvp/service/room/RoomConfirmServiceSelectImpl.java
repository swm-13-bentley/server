package com.schedch.mvp.service.room;

import com.schedch.mvp.dto.email.EmailReq;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.email.AwsMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomConfirmServiceSelectImpl implements RoomConfirmService{

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
        room.confirmRoom(confirmedDate, startTime, endTime);

        //참가자들 중 이메일을 등록한 사람들에게 메일을 발송한다
        List<Participant> emailRegisteredParticipantList = participantList.stream()
                .filter(participant -> participant.getUser() != null && participant.getUser().isReceiveEmail())
                .collect(Collectors.toList());
        sendEmail(roomUuid, confirmedDate, startTime, endTime, emailRegisteredParticipantList);

        log.info("S: confirmRoom / roomUuid = {}", roomUuid);
    }

    public void sendEmail(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Participant> participantList) {
        log.info("P: sendEmail / roomUuid = {}", roomUuid);
        EmailReq emailReq;
        if (startTime == null || endTime == null) {
            emailReq = getDayEmailReq(roomUuid, confirmedDate);
        }
        else {
            emailReq = getTimeEmailReq(roomUuid, confirmedDate, startTime, endTime);
        }

        participantList.stream().forEach(p -> {
            String mailTo = p.getUser().getEmail();
            setEmailReqInfo(emailReq, mailTo, p);
            awsMailService.sendEmailBySes(emailReq);
        });
        log.info("S: sendEmail / roomUuid = {}", roomUuid);
    }
}
