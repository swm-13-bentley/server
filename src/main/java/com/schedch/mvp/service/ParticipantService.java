package com.schedch.mvp.service;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.exception.FullMemberException;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final RoomService roomService;

    public Participant findUnSignedParticipantAndValidate(String roomUuid, String participantName, String password) throws IllegalAccessException {
        Room room = roomService.getRoomWithParticipants(roomUuid);
        List<Participant> foundParticipant = room.findUnSignedParticipant(participantName);

        if(foundParticipant.isEmpty()) {//신규 유저 -> 유저 등록해야 함
            if(!room.canAddMember()) {//member limit
                log.warn("E: findUnSignedParticipantAndValidate / room is full / roomId = {}", room.getId());
                throw new FullMemberException(ErrorMessage.fullMemberForUuid(roomUuid));
            }

            Participant newParticipant = new Participant(participantName, password, false);
            room.addParticipant(newParticipant);
            return newParticipant;
        }

        Participant participant = foundParticipant.get(0);
        if(!participant.checkPassword(password)) { //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
            log.warn("E: findUnSignedParticipantAndValidate / password is wrong / participantName = {}, password = {}, roomUuid = {}", participantName, password, roomUuid);
            throw new IllegalAccessException(ErrorMessage.passwordIsWrong(participantName, password, roomUuid));
        }

        return participant;
    }

    public void saveParticipantAvailable(String roomUuid, String participantName, List<TimeBlockDto> available) {
        Room room = roomService.getRoomWithParticipants(roomUuid);

        Participant participant = getUnSignedParticipantFromRoom(room, participantName);
        participant.emptySchedules();

        LocalTime roomStartTime = room.getStartTime();
        available.stream()
                .forEach(timeBlockDto -> {
                    TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto, roomStartTime).stream()
                            .forEach(schedule -> participant.addSchedule(schedule));
                });
    }

    public void saveDayParticipantAvailable(String roomUuid, String participantName, List<LocalDate> localDateList) {
        Room room = roomService.getRoomWithParticipants(roomUuid);

        Participant participant = getUnSignedParticipantFromRoom(room, participantName);
        participant.emptySchedules();

        Participant finalParticipant = participant;
        localDateList.stream().forEach(localDate -> {
            finalParticipant.addSchedule(new Schedule(localDate));
        });
    }

    public void registerAlarmEmail(String roomUuid, String participantName, String alarmEmail) {
        Room room = roomService.getRoomWithParticipants(roomUuid);
        Participant participant = getUnSignedParticipantFromRoom(room, participantName);

        participant.setAlarmEmail(alarmEmail);
    }

    private Participant getUnSignedParticipantFromRoom(Room room, String participantName) {
        List<Participant> participantList = room.findUnSignedParticipant(participantName);
        if (participantList.isEmpty()) {
            String roomUuid = room.getUuid();
            log.warn("E: saveParticipantAvailable / participant name not in room / participantName = {}, roomUuid = {}", participantName, roomUuid);
            throw new NoSuchElementException(ErrorMessage.participantNameNotInRoom(participantName, roomUuid));
        }

        return participantList.get(0);
    }
}
