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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final RoomService roomService;

    public Participant getParticipant(String roomUuid, String participantName, String password) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);
        Optional<Participant> participantOptional = findOptionalParticipantByRoomAndName(room, participantName, true);

        if(participantOptional.isEmpty()) {//신규 유저 -> 유저 등록해야 함
            if(!room.canAddMember()) {//member limit
                log.warn("E: findParticipantByName / room is full / roomId = {}", room.getId());
                throw new FullMemberException(ErrorMessage.fullMemberForUuid(roomUuid));
            }

            Participant newParticipant = new Participant(participantName, password, false);
            room.addParticipant(newParticipant);
            return newParticipant;
        }

        Participant participant = participantOptional.get();
        if(!participant.checkPassword(password)) {  //존재하는 유저지만, 비밀번호가 다르다.
            log.warn("E: findParticipantByName / password is wrong / participantName = {}, password = {}, roomUuid = {}", participantName, password, roomUuid);
            throw new IllegalAccessException(ErrorMessage.passwordIsWrong(participantName, password, roomUuid));
        }

        return participant; //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
    }

    public void saveParticipantAvailable(String roomUuid, String participantName, List<TimeBlockDto> available) {
        Room room = roomService.getRoom(roomUuid);
        Participant participant = findParticipantByRoomAndName(room, participantName, true);
        participant.emptySchedules();

        LocalTime roomStartTime = room.getStartTime();
        for (TimeBlockDto timeBlockDto : available) {
            List<Schedule> scheduleList = TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto, roomStartTime);
            for (Schedule schedule : scheduleList) {
                participant.addSchedule(schedule);
            }
        }
    }

    public void saveDayParticipantAvailable(String roomUuid, String participantName, List<LocalDate> localDateList) {
        Room room = roomService.getRoom(roomUuid);
        Participant participant = findParticipantByRoomAndName(room, participantName, true);
        participant.emptySchedules();

        for (LocalDate localDate : localDateList) {
            participant.addSchedule(new Schedule(localDate));
        }
    }

    public void registerAlarmEmail(String roomUuid, String participantName, String alarmEmail) {
        Room room = roomService.getRoom(roomUuid);
        Participant participant = findParticipantByRoomAndName(room, participantName, false);

        participant.setAlarmEmail(alarmEmail);
    }

    /**
     * 방, 이름으로 참석자를 조회. 참석자가 없을 경우 예외를 던짐.
     * @param room
     * @param participantName
     * @param withSchedule - Schedule join fetch 여부
     * @return Participant - 참석자
     * @throws NoSuchElementException - 약속 방에 해당 이름의 참석자가 존재하지 않음
     */
    public Participant findParticipantByRoomAndName(Room room, String participantName, boolean withSchedule) {
        List<Participant> foundParticipant;
        if( withSchedule )
            foundParticipant = participantRepository.findParticipantByRoomAndParticipantNameWithSchedules(room, participantName);
        else
            foundParticipant = participantRepository.findParticipantByRoomAndParticipantName(room, participantName);

        if(foundParticipant.isEmpty()) {//해당 이름의 유저는 없음
            log.warn("E: findParticipantByRoomAndName / NoSuchElementException / roomUuid = {}, participantName = {}", room.getUuid(), participantName);
            throw new NoSuchElementException(ErrorMessage.participantNameNotInRoom(participantName, room.getUuid()));
        }

        if(foundParticipant.size() >= 2) { //중복 유저가 존재
            log.error("E: findParticipantByRoomAndName / roomUuid = {}, participantName = {}", room.getUuid(), participantName);
        }

        Participant participant = foundParticipant.get(0);
        return participant;
    }

    /**
     * 방, 이름으로 참석자를 조회. 참석자가 없을 경우 Optional.empty()를 반환.
     * @param room
     * @param participantName
     * @param withSchedule
     * @return
     */
    public Optional<Participant> findOptionalParticipantByRoomAndName(Room room, String participantName, boolean withSchedule) {
        List<Participant> foundParticipant;
        if( withSchedule )
            foundParticipant = participantRepository.findParticipantByRoomAndParticipantNameWithSchedules(room, participantName);
        else
            foundParticipant = participantRepository.findParticipantByRoomAndParticipantName(room, participantName);

        if(foundParticipant.isEmpty()) {//신규 유저 -> 유저 등록해야 함
            return Optional.empty();
        }

        if(foundParticipant.size() >= 2) { //중복 유저가 존재
            log.error("E: findOptionalParticipantByRoomAndName / roomUuid = {}, participantName = {}", room.getUuid(), participantName);
        }

        Participant participant = foundParticipant.get(0);
        return Optional.of(participant);
    }
}
