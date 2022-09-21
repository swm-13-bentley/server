package com.schedch.mvp.service.user;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.dto.user.UserAvailableDayReq;
import com.schedch.mvp.dto.user.UserAvailableTimeReq;
import com.schedch.mvp.exception.UserNotInRoomException;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.model.User;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserParticipantService {

    private final RoomService roomService;
    private final UserService userService;
    private final ParticipantRepository participantRepository;

    public void saveAvailableTimeToRoom(String userEmail, String roomUuid, UserAvailableTimeReq userAvailableTimeReq) {
        Room room = roomService.getRoom(roomUuid);
        User user = userService.getUserByEmail(userEmail);
        Participant participant = getParticipant(user, room);

        participant.emptySchedules();
        participant.setParticipantName(userAvailableTimeReq.getParticipantName());

        List<TimeBlockDto> timeBlockDtoList = userAvailableTimeReq.getAvailable();
        timeBlockDtoList.stream().forEach(
                block -> {
                    List<Schedule> scheduleList = TimeAdapter.changeTimeBlockDtoToSchedule(block, room.getStartTime());
                    scheduleList.stream().forEach(schedule -> {participant.addSchedule(schedule);});
                }
        );
    }

    public void saveAvailableDayToRoom(String userEmail, String roomUuid, UserAvailableDayReq userAvailableDayReq) {
        Participant participant = getParticipant(userEmail, roomUuid);

        participant.emptySchedules();
        participant.setParticipantName(userAvailableDayReq.getParticipantName());

        List<LocalDate> availableDates = userAvailableDayReq.getAvailableDates();
        availableDates.stream().forEach(
                date -> {
                    participant.addSchedule(new Schedule(date));
                }
        );


    }

    public void changeParticipantName(String userEmail, String roomUuid, String changedName) {
        Participant participant = getParticipant(userEmail, roomUuid);
        participant.setParticipantName(changedName);
    }

    public void changeRoomTitle(String userEmail, String roomUuid, String changedTitle) {
        Participant participant = getParticipant(userEmail, roomUuid);
        participant.setRoomTitle(changedTitle);
    }

    public Participant getParticipant(String userEmail, String roomUuid) {
        User user = userService.getUserByEmail(userEmail);
        Room room = roomService.getRoom(roomUuid);
        return getParticipant(user, room);
    }

    public Participant getParticipant(User user, Room room) {
        Optional<Participant> participantOptional = participantRepository.findByUserAndRoom(user, room);
        if (participantOptional.isEmpty()) {
            throw new UserNotInRoomException(ErrorMessage.userNotInRoom(room.getUuid()));
        }

        return participantOptional.get();
    }

}
