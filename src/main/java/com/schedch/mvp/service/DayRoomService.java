package com.schedch.mvp.service;

import com.schedch.mvp.dto.room.DayRoomTopRes;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DayRoomService {

    private final RoomService roomService;

    public List<DayRoomTopRes> getTopAvailableDate(String roomUuid, int max) {
        Room room = roomService.getRoom(roomUuid);

        List<LocalDate> localDateList = room.getLocalDateList();

        HashMap<LocalDate, PriorityQueue<String>> countMap = new HashMap<>();
        localDateList.stream().forEach(localDate -> countMap.put(localDate, new PriorityQueue<>()));

        List<Participant> participantList = room.getParticipantList();
        participantList.stream().forEach(participant -> {
            participant.getScheduleList().stream().forEach(schedule -> {
                LocalDate availableDate = schedule.getAvailableDate();
                if(countMap.containsKey(availableDate)) {
                    PriorityQueue<String> pq = countMap.get(availableDate);
                    pq.add(participant.getParticipantName());
                }
            });
        });

        List<DayRoomTopRes> dayRoomTopResList = new ArrayList<>();

        countMap.forEach((k, v) -> {
            if(v.size() > 0) {
                dayRoomTopResList.add(new DayRoomTopRes(k, v));
            }
        });

        dayRoomTopResList.sort(new Comparator<DayRoomTopRes>() {
            @Override
            public int compare(DayRoomTopRes o1, DayRoomTopRes o2) {
                if(o1.getParticipants().size() == o2.getParticipants().size()) {
                    return o1.getAvailableDate().compareTo(o2.getAvailableDate());
                } else {
                    return o2.getParticipants().size() - o1.getParticipants().size();
                }
            }
        });

        return dayRoomTopResList.subList(0, Math.min(dayRoomTopResList.size(), max));
    }
}
