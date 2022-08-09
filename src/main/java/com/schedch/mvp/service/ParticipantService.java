package com.schedch.mvp.service;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final RoomService roomService;
    private final TimeAdapter timeAdapter;

    public ParticipantResponseDto findUnSignedParticipantAndValidate(
            String roomUuid, String participantName, String password) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);
        List<Participant> foundParticipant = room.findUnSignedParticipant(participantName);

        if(foundParticipant.isEmpty()) {
            //신규 유저 -> 유저 등록해야 함
            Participant newParticipant = new Participant(participantName, password, false);
            room.addParticipant(newParticipant);
            return new ParticipantResponseDto(newParticipant);
        }

        Participant participant = foundParticipant.get(0);

        if(participant.checkPassword(password)) { //기존 유저가 맞음 -> 기존 시간 돌려주면 됨
            return new ParticipantResponseDto(participant);
        }
        else { //기존 유저이나, 비밀번호가 틀렸음
            throw new IllegalAccessException("password is incorrect for participant: " + participantName);
        }
    }

    public void saveParticipantAvailable(String roomUuid, AvailableRequestDto availableRequestDto) {
        Room room = roomService.getRoom(roomUuid);
        String participantName = availableRequestDto.getParticipantName();

        Participant participant = participantRepository.findParticipantByParticipantNameAndRoom(participantName, room)
                .orElseThrow(() -> new NoSuchElementException(String.format("Participant not found for name: %s", participantName)));

        participant.emptySchedules();

        LocalTime roomStartTime = room.getStartTime();
        availableRequestDto.getAvailable().stream()
                .forEach(timeBlockDto -> {
                    changeTimeBlockDtoToSchedule(timeBlockDto, roomStartTime).stream()
                            .forEach(schedule -> participant.addSchedule(schedule));

                });
    }

    /**
     * 같은 날짜에 이어져있는 time int들을 비연속적이라면, 분리해서 schedule로 변환
     * @param timeBlockDto
     * @return
     */
    public List<Schedule> changeTimeBlockDtoToSchedule(TimeBlockDto timeBlockDto, LocalTime roomStartTime) {
        List<Schedule> scheduleList = new ArrayList<>();
        LocalDate availableDate = timeBlockDto.getAvailableDate();
        List<Integer> availableTimeList = timeBlockDto.getAvailableTimeList();
        if(!availableTimeList.isEmpty()) {
            int start = availableTimeList.get(0);
            int end = start;

            for (int i = 1; i <= availableTimeList.size(); i++) {
                if(i == availableTimeList.size()) {
                    LocalTime startTime = timeAdapter.startBlock2lt(start);
                    scheduleList.add(new Schedule(availableDate, startTime, timeAdapter.endBlock2lt(end), roomStartTime));
                    return scheduleList;
                }
                if (availableTimeList.get(i) != end + 1) {//불연속 or 마지막
                    scheduleList.add(new Schedule(availableDate, timeAdapter.startBlock2lt(start), timeAdapter.endBlock2lt(end), roomStartTime));
                    start = availableTimeList.get(i);
                }
                end = availableTimeList.get(i);
            }
        }
        return scheduleList;
    }

    public List<ParticipantResponseDto> findAllParticipantsInRoom(String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        List<Participant> participantList = room.getParticipantList();

        return participantList.stream()
                .map(participant -> new ParticipantResponseDto(participant))
                .collect(Collectors.toList());
    }
}
