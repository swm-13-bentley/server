package com.schedch.mvp.service.room;

import com.schedch.mvp.dto.email.EmailReq;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.email.AwsMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 이 implementation에서는 이메일을 등록한 모든 사용자에게 알림을 보낸다.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomConfirmServiceAllSendImpl implements RoomConfirmService{

    private final AwsMailService awsMailService;
    private final RoomService roomService;

    public Map<String, List> findInRangeParticipantList(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime) {
        return null;
    }

    public void confirmRoom(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Long> participantIdList) {
        log.info("P: confirmRoom / roomUuid = {}", roomUuid);

        //방과 참가자들을 불러온다
        Room room = roomService.getRoomWithParticipants(roomUuid);
        List<Participant> emailReceivingParticipants = room.confirmRoom(confirmedDate, startTime, endTime);

        //유저 참가자들 중
        sendEmail(roomUuid, confirmedDate, startTime, endTime, emailReceivingParticipants);

        log.info("S: confirmRoom / roomUuid = {}", roomUuid);
    }

    public void sendEmail(String roomUuid, LocalDate confirmedDate, LocalTime startTime, LocalTime endTime, List<Participant> emailReceivingParticipants) {
        log.info("P: sendEmail / roomUuid = {}", roomUuid);
        EmailReq emailReq;
        if (startTime == null || endTime == null) {
            emailReq = getDayEmailReq(roomUuid, confirmedDate);
        }
        else {
            emailReq = getTimeEmailReq(roomUuid, confirmedDate, startTime, endTime);
        }

        emailReceivingParticipants.stream().forEach(participant -> {
            String email = participant.getUser().getEmail();
            setEmailReqInfo(emailReq, email, participant);
            awsMailService.sendEmailBySes(emailReq);
        });
        log.info("S: sendEmail / roomUuid = {}", roomUuid);
    }
}
