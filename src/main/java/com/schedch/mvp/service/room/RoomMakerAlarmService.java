package com.schedch.mvp.service.room;

import com.schedch.mvp.dto.email.MakerAlarmReq;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.service.email.AwsMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomMakerAlarmService {

    private final AwsMailService awsMailService;

    public void sendMakerAlarm(Room room) {
        room.setAlarmSent(true);
        MakerAlarmReq makerEmailReq = getMakerEmailReq(room);
        awsMailService.sendEmailToMaker(makerEmailReq);
    }

    private MakerAlarmReq getMakerEmailReq(Room room) {
        String mailTo = room.getAlarmEmail();
        String roomTitle = room.getTitle();
        String roomUuid = room.getUuid();
        int alarmNumber = room.getAlarmNumber();
        boolean isDayOnly = room.getStartTime() == null;
        String roomLink = isDayOnly ? "https://mannatime.io/ko/date/entry/" + roomUuid + "?invitation=true" : "https://mannatime.io/ko/entry/" + roomUuid + "?invitation=true";

        return MakerAlarmReq.builder()
                .mailTo(mailTo)
                .roomTitle(roomTitle)
                .roomLink(roomLink)
                .alarmNumber(alarmNumber)
                .build();
    }
}
