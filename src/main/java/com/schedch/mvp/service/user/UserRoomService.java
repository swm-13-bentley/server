package com.schedch.mvp.service.user;

import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.dto.user.UserParticipatingRoomRes;
import com.schedch.mvp.exception.FullMemberException;
import com.schedch.mvp.model.*;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.repository.RoomRepository;
import com.schedch.mvp.repository.UserRepository;
import com.schedch.mvp.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;

    public ParticipantResponseDto entry(String userEmail, String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        User user = getUserByEmail(userEmail);

        if (room.contains(user)) {
            Participant participant = participantRepository.findByUserAndRoom(user, room).get();
            return new ParticipantResponseDto(participant);
        }

        if (!room.canAddMember()) { // Case: room is already full
            throw new FullMemberException(ErrorMessage.fullMemberForUuid(roomUuid));
        }

        Participant participant = new Participant(user);
        user.addParticipant(participant);
        room.addParticipant(participant);
        return new ParticipantResponseDto(participant);
    }

    public List<UserParticipatingRoomRes> getAllRooms(String userEmail, boolean confirmed) {
        List<UserParticipatingRoomRes> resList = new ArrayList<>();
        List<Participant> participantList = participantRepository.findAllByUserEmailJoinFetchRoom(userEmail);

        // get room id list
        List<Long> roomIdList = participantList.stream()
                .map(participant -> {return participant.getRoom().getId();})
                .collect(Collectors.toList());

        // join fetch dates in roomDate map
        List<Room> allInIdListJoinFetchRoomDates = roomRepository.findAllInIdListJoinFetchRoomDates(roomIdList);
        Map<Long, List<LocalDate>> roomDateMap = allInIdListJoinFetchRoomDates.stream()
                .collect(Collectors.toMap(
                        Room::getId,
                        room -> {
                            return room.getRoomDates().stream().map(RoomDate::getScheduledDate).collect(Collectors.toList());
                        }
                ));

        // join fetch participantName map
        List<Room> allInIdListJoinFetchParticipantList = roomRepository.findAllInIdListJoinFetchParticipantList(roomIdList);
        Map<Long, List<String>> participantNameMap = allInIdListJoinFetchParticipantList.stream()
                .collect(Collectors.toMap(
                        Room::getId,
                        room -> {
                            return room.getParticipantList().stream().map(Participant::getParticipantName).collect(Collectors.toList());
                        }
                ));

        participantList.stream().filter(participant -> participant.getRoom().isConfirmed() == confirmed)
                .forEach(participant -> {
                    Long roomId = participant.getRoom().getId();
                    UserParticipatingRoomRes resItem = new UserParticipatingRoomRes(participant);
                    resItem.setRoomDates(roomDateMap.get(roomId));
                    resItem.setParticipantNames(participantNameMap.get(roomId));

                    List<TopTime> topAvailableTimeAndNames = roomService.getTopAvailableTimeAndNames(participant.getRoom().getUuid(), 1);
                    resItem.setTopCountResByTopTime(topAvailableTimeAndNames);

                    resList.add(resItem);
                });

        return resList;
    }

    /**
     * should only call when user existence is already validated
     * ex) user existence validated through jwt token check
     * @param userEmail
     * @return
     */
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).get();
    }
}
