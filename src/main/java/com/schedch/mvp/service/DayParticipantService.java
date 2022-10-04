package com.schedch.mvp.service;

import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DayParticipantService {

    private final RoomService roomService;
    private final ParticipantRepository participantRepository;

    public Participant findParticipant(String roomUuid, String participantName, String password) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);
        List<Participant> foundParticipant = room.findUnSignedParticipant(participantName);

        if(foundParticipant.isEmpty()) { //신규 유저 -> 유저 등록해야 함
            log.warn("E: findParticipant / no such participant in room / participantName = {}, roomUuid = {}", participantName, roomUuid);
            throw new NoSuchElementException(ErrorMessage.participantNameNotInRoom(participantName, roomUuid));
        }

        Participant participant = foundParticipant.get(0);
        if(!participant.checkPassword(password)) { //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
            log.warn("E: findParticipant / password is wrong / participantName = {}, password = {}, roomId = {}", participantName, password, room.getId());
            throw new IllegalAccessException(ErrorMessage.passwordIsWrong(participantName, password, roomUuid));
        }

        return participant;
    }

    public void saveParticipantAvailable(String roomUuid, String participantName, String password, List<LocalDate> localDateList) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);

        Optional<Participant> participantOptional = participantRepository.findParticipantByParticipantNameAndRoomAndIsSignedIn(participantName, room, false);
        Participant participant = null;
        if (participantOptional.isEmpty()) {//없는 참가자일 경우 새로이 추가
            participant = new Participant(participantName, password, false);
            room.addParticipant(participant);
        }

        participant = participantOptional.get();
        if (!participant.checkPassword(password)) {
            log.warn("E: findParticipant / password is wrong / participantName = {}, password = {}, roomUuid = {}", participantName, password, roomUuid);
            throw new IllegalAccessException(ErrorMessage.passwordIsWrong(participantName, password, roomUuid));
        }
        participant.emptySchedules();

        Participant finalParticipant = participant;
        localDateList.stream().forEach(localDate -> {
            finalParticipant.addSchedule(new Schedule(localDate));
        });
    }


}
