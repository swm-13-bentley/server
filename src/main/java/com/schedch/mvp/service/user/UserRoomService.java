package com.schedch.mvp.service.user;

import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.room.DayRoomTopRes;
import com.schedch.mvp.dto.user.UserParticipatingRoomConfirmedRes;
import com.schedch.mvp.dto.user.UserParticipatingRoomRes;
import com.schedch.mvp.exception.FullMemberException;
import com.schedch.mvp.exception.UserNotInRoomException;
import com.schedch.mvp.model.*;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.repository.RoomRepository;
import com.schedch.mvp.service.DayRoomService;
import com.schedch.mvp.service.ParticipantService;
import com.schedch.mvp.service.RoomService;
import com.schedch.mvp.service.room.RoomMakerAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserRoomService {

    private final UserService userService;
    private final RoomService roomService;
    private final DayRoomService dayRoomService;
    private final RoomRepository roomRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final RoomMakerAlarmService roomMakerAlarmService;

    public Participant entry(String userEmail, String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        User user = userService.getUserByEmail(userEmail);

        Optional<Participant> participantOptional = participantRepository.findByUserAndRoom(user, room);
        if (participantOptional.isPresent()) {// Case 1: user is already in room
            Participant participant = participantOptional.get();
            return participant;
        }

        if (!room.canAddMember()) { // Case 2: room is already full
            log.warn("E: entry / room is full / roomId = {}", room.getId());
            throw new FullMemberException(ErrorMessage.fullMemberForUuid(roomUuid));
        }

        // Case 3: new user enters room
        String participantName = user.getUsername();
        Optional<Participant> existingNameOptional = participantService.findOptionalParticipantByRoomAndName(room, participantName, false);
        if (existingNameOptional.isPresent()) { //Case 3-1: participantName already exists
            log.info("E: entry / already existing name / userId = {}, roomUuid = {}, participantName = {}", user.getId(), roomUuid, participantName);
            throw new IllegalArgumentException(ErrorMessage.alreadyExistingParticipantName(participantName));
        }

        // Case 3-2: create new user
        Participant participant = new Participant(user);
        user.addParticipant(participant);
        room.addParticipant(participant);

        if(room.getAlarmNumber() != 0 && room.getParticipantList().size() >= room.getAlarmNumber()) { //send email to maker
            Thread thread = new Thread(() -> {
                roomMakerAlarmService.sendMakerAlarm(room);
            });
            thread.start();
        }

        return participant;
    }

    public void exitRoom(String userEmail, String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        User user = userService.getUserByEmail(userEmail);

        Optional<Participant> participantOptional = participantRepository.findByUserAndRoom(user, room);
        if (participantOptional.isEmpty()) {
            log.warn("E: exitRoom / user is not in room / userId = {}, roomId = {}", user.getId(), room.getId());
            throw new UserNotInRoomException(ErrorMessage.userNotInRoom(roomUuid));
        }

        Participant participant = participantOptional.get();
        participantRepository.delete(participant);
    }

    public Long getParticipantIdInRoom(String userEmail, String roomUuid) {
        Room room = roomService.getRoom(roomUuid);
        User user = userService.getUserByEmail(userEmail);

        Optional<Participant> participantOptional = participantRepository.findByUserAndRoom(user, room);
        if (participantOptional.isEmpty()) {
            log.warn("E: getParticipantIdInRoom / user is not in room / userId = {}, roomId = {}", user.getId(), room.getId());
            throw new UserNotInRoomException(ErrorMessage.userNotInRoom(roomUuid));
        }

        Participant participant = participantOptional.get();
        return participant.getId();
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
                    boolean isDayOnly = participant.getRoom().getStartTime() == null;
                    Long roomId = participant.getRoom().getId();

                    UserParticipatingRoomRes resItem = new UserParticipatingRoomRes(participant);
                    resItem.setRoomDates(roomDateMap.get(roomId));
                    resItem.setParticipants(participantNameMap.get(roomId));

                    if(isDayOnly) {
                        List<DayRoomTopRes> topAvailableDate = dayRoomService.getTopAvailableDate(participant.getRoom().getUuid(), 1);
                        resItem.setTopCountResByTopDate(topAvailableDate);

                    }
                    else {
                        List<TopTime> topAvailableTimeAndNames = roomService.getTopAvailableTimeAndNames(participant.getRoom().getUuid(), 1);
                        resItem.setTopCountResByTopTime(topAvailableTimeAndNames);
                    }

                    resList.add(resItem);
                });

        return resList;
    }

    public List<UserParticipatingRoomConfirmedRes> getAllConfirmedRoomInfo(String userEmail) {
        List<Participant> participantList = participantRepository.findAllByUserEmailJoinFetchRoom(userEmail);
        List<Participant> confirmedParticipantList = participantList.stream().filter(participant -> participant.getRoom().isConfirmed() == true).collect(Collectors.toList());

        // get room id list
        List<Long> roomIdList = participantList.stream()
                .map(participant -> {return participant.getRoom().getId();})
                .collect(Collectors.toList());

        // join fetch participantName map
        List<Room> allInIdListJoinFetchParticipantList = roomRepository.findAllInIdListJoinFetchParticipantList(roomIdList);
        Map<Long, List<String>> participantNameMap = allInIdListJoinFetchParticipantList.stream()
                .collect(Collectors.toMap(
                        Room::getId,
                        room -> {
                            return room.getParticipantList().stream().map(Participant::getParticipantName).collect(Collectors.toList());
                        }
                ));

        List<UserParticipatingRoomConfirmedRes> resList = new ArrayList<>();
        for (Participant participant : confirmedParticipantList) {
            Room room = participant.getRoom();
            List<String> participantNameList = participantNameMap.get(room.getId());
            UserParticipatingRoomConfirmedRes resItem = new UserParticipatingRoomConfirmedRes(participant.getRoomTitle(), room, participantNameList);
            resList.add(resItem);
        }

        return resList;
    }
}
