package com.schedch.mvp.service;

import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DayParticipantService {

    private final RoomService roomService;
    private final ParticipantRepository participantRepository;

    public Participant findParticipant(
            String roomUuid, String participantName, String password) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);
        List<Participant> foundParticipant = room.findUnSignedParticipant(participantName);

        if(foundParticipant.isEmpty()) {
            //신규 유저 -> 유저 등록해야 함
            throw new NoSuchElementException(String.format("No participant found for participant name: {%s}", participantName));
        }

        Participant participant = foundParticipant.get(0);

        if(participant.checkPassword(password)) { //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
            return participant;
        }
        else { //기존 유저이나, 비밀번호가 틀렸음
            throw new IllegalAccessException("password is incorrect for participant: " + participantName);
        }
    }

    public void saveParticipantAvailable(String roomUuid, String participantName, String password, List<LocalDate> localDateList) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);

        Optional<Participant> participantOptional = participantRepository.findParticipantByParticipantNameAndRoomAndIsSignedIn(participantName, room, false);
        Participant participant = null;
        if (participantOptional.isEmpty()) {//없는 참가자일 경우 새로이 추가
            participant = new Participant(participantName, password, false);
            room.addParticipant(participant);
        } else {//기존 참가자일 경우 기존 입력을 초기화
            participant = participantOptional.get();
            if (participant.checkPassword(password) == false) {
                throw new IllegalAccessException(String.format("password is incorrect for participant: {%s}", participantName));
            }
            participant.emptySchedules();
        }

        Participant finalParticipant = participant;
        localDateList.stream().forEach(localDate -> {
            finalParticipant.addSchedule(new Schedule(localDate));
        });
    }


}
