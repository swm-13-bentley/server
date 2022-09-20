package com.schedch.mvp.service;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.exception.FullMemberException;
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

    public ParticipantResponseDto findUnSignedParticipantAndValidate(
            String roomUuid, String participantName, String password) throws IllegalAccessException {
        Room room = roomService.getRoom(roomUuid);
        List<Participant> foundParticipant = room.findUnSignedParticipant(participantName);

        if(foundParticipant.isEmpty()) {//신규 유저 -> 유저 등록해야 함
            if(!room.canAddMember()) {//member limit
                throw new FullMemberException(ErrorMessage.fullMemberForUuid(roomUuid));
            }

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

        Participant participant = participantRepository.findParticipantByParticipantNameAndRoomAndIsSignedIn(participantName, room, false)
                .orElseThrow(() -> new NoSuchElementException(String.format("Participant not found for name: %s", participantName)));

        participant.emptySchedules();

        LocalTime roomStartTime = room.getStartTime();
        availableRequestDto.getAvailable().stream()
                .forEach(timeBlockDto -> {
                    TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto, roomStartTime).stream()
                            .forEach(schedule -> participant.addSchedule(schedule));

                });
    }


    public List<ParticipantResponseDto> findAllParticipantsInRoom(String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        List<Participant> participantList = room.getParticipantList();

        return participantList.stream()
                .map(participant -> new ParticipantResponseDto(participant))
                .collect(Collectors.toList());
    }
}
