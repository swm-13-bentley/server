package com.schedch.mvp.service;

import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    public ParticipantResponseDto findUnSignedParticipantAndValidate(
            String roomUuid, String participantName, String password) throws IllegalAccessException, NoSuchElementException {

        //방에 참여하는 참여자 정보 다 가져옴
        Optional<Room> roomOptional = roomRepository.findByUuid(roomUuid);
        Room room = roomOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid)));
        //이 부분은 RoomService 불러서 해야할지, 근데 그러면 exception throw 가 명시적으로 보이지 않아서 걱정.

        List<Participant> foundParticipant = room.getParticipantList().stream()
                .filter(p -> p.isSignedIn() == false && p.getParticipantName().equals(participantName))
                .collect(Collectors.toList());

        if(foundParticipant.isEmpty()) {
            //신규 유저 -> 유저 등록해야 함
            Participant newParticipant = new Participant(participantName, password, false);
            room.addParticipant(newParticipant);
            return new ParticipantResponseDto(newParticipant);
        }

        Participant participant = foundParticipant.get(0);

        if(participant.checkPassword(password)) {
            //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
            return new ParticipantResponseDto(participant);
        }
        else {
            //기존 유저이나, 비밀번호가 틀렸음
            throw new IllegalAccessException("password is incorrect for participant: " + participantName);
       }
    }

    public void saveParticipantAvailable(String roomUuid, AvailableRequestDto availableRequestDto) {
        Room room = roomRepository.findByUuid(roomUuid).orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid)));

        String participantName = availableRequestDto.getParticipantName();

        Participant participant = participantRepository.findParticipantByParticipantNameAndRoom(participantName, room)
                .orElseThrow(() -> new NoSuchElementException(String.format("Participant not found for name: %s", participantName)));

        participant.emptySchedules();

        availableRequestDto.getAvailable().stream()
                .forEach(timeBlockDto -> {
                    changeTimeBlockDtoToSchedule(timeBlockDto).stream()
                            .forEach(schedule -> participant.addSchedule(schedule));

                });
    }

    public List<Schedule> changeTimeBlockDtoToSchedule(TimeBlockDto timeBlockDto) {
        List<Schedule> scheduleList = new ArrayList<>();
        LocalDate availableDate = timeBlockDto.getAvailableDate();
        List<Integer> availableTimeList = timeBlockDto.getAvailableTimeList();
        if(!availableTimeList.isEmpty()) {
            int start = availableTimeList.get(0);
            int end = start;

            for (int i = 1; i <= availableTimeList.size(); i++) {
                if(i == availableTimeList.size()) {
                    scheduleList.add(new Schedule(availableDate, start, end));
                    return scheduleList;
                }
                if (availableTimeList.get(i) != end + 1) {//불연속 or 마지막
                    scheduleList.add(new Schedule(availableDate, start, end));
                    start = availableTimeList.get(i);
                }
                end = availableTimeList.get(i);
            }
        }
        return scheduleList;
    }

    public List<ParticipantResponseDto> findAllParticipantsInRoom(String roomUuid) {
        Room room = roomRepository.findByUuid(roomUuid).orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid)));

        List<Participant> participantList = room.getParticipantList();

        return participantList.stream()
                .map(participant -> new ParticipantResponseDto(participant))
                .collect(Collectors.toList());
    }
}
