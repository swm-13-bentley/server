package com.schedch.mvp.service;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final TimeAdapter timeAdapter;

    public String createRoom(Room room) {
        Room save = roomRepository.save(room);
        return save.getUuid();
    }

    public Room getRoom(String roomUuid) {
        Optional<Room> roomOptional = roomRepository.findByUuid(roomUuid);
        Room room = roomOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid))
        );
        return room;
    }

    public List<TimeCount> getTopAvailableTime(String roomUuid, int max) {
        // 방 내 모든 시간 불러온 후
        // 시간을 timeblock으로 바꾸고
        // 모든 날짜에, 모든 시간에 각각 몇명이나 있는지 파악하기
        // 2차원 배열 순회하면서 인원수 체크하고, 붙어있으면 붙은 일정으로 내보내기
        Room room = getRoom(roomUuid);
        List<RoomDate> roomDates = room.getRoomDates();
        int roomStartTimeBlock = timeAdapter.localTime2TimeBlockInt(room.getStartTime());

        LocalTime roomEndTime = room.getEndTime();
        int roomEndTimeBlock = timeAdapter.localTime2TimeBlockInt(roomEndTime);

        int colNum = 0;
        HashMap<LocalDate, Integer> colNumMap = new HashMap<>();
        for (RoomDate roomdate: roomDates) {
            colNumMap.put(roomdate.getScheduledDate(), colNum++);
        }

        int rowSize = roomEndTimeBlock - roomStartTimeBlock + 1;
        int colSize = roomDates.size();

        //극단적으로 생각해보면 row, col 바꾸는게 나을지도? 디스크에서 읽는 속도 생각하면
        int[][] board = new int[rowSize+1][colSize];

        List<Participant> participantList = room.getParticipantList();
        for(Participant participant : participantList) {
            List<Schedule> scheduleList = participant.getScheduleList();
            for(Schedule schedule : scheduleList) {
                LocalDate availableDate = schedule.getAvailableDate();
                int startBlock = timeAdapter.localTime2TimeBlockInt(schedule.getStartTime());
                int endBlock = timeAdapter.localTime2TimeBlockInt(schedule.getEndTime());

                int colIdx = colNumMap.get(availableDate);
                board[startBlock - roomStartTimeBlock][colIdx]++;
                board[endBlock - roomStartTimeBlock + 1][colIdx]--;
            }
        }

        for (int j = 0; j < colSize; j++) {
            for (int i = 1; i <= rowSize; i++) {
                board[i][j] += board[i - 1][j];
            }
        }

        List<TimeCount> timeCountList = new ArrayList<>();
        for (int j = 0; j < colSize; j++) {
            int count = board[0][j];
            int starting = 0;
            for (int i = 0; i <= rowSize; i++) {
                if(i == rowSize) {
                    TimeCount t = new TimeCount(count, roomDates.get(j).getScheduledDate(), roomStartTimeBlock + starting, rowSize-1);
                    timeCountList.add(t);
                    break;
                }
                int nowCount = board[i][j];
                if(count != nowCount) {
                    TimeCount t = new TimeCount(count, roomDates.get(j).getScheduledDate(), roomStartTimeBlock + starting, roomStartTimeBlock + i - 1);
                    timeCountList.add(t);
                    count = nowCount;
                    starting = i;
                }
            }
        }

        Collections.sort(timeCountList);

        return timeCountList.subList(0, Math.min(timeCountList.size(), max));
    }

    public class TimeCount implements Comparable<TimeCount> {
        int count;
        LocalDate availableDate;
        String startTime;
        String endTime;

        public TimeCount(int count, LocalDate availableDate, int start, int end) {
            this.count = count;
            this.availableDate = availableDate;
            this.startTime = timeAdapter.startBlock2Str(start);
            this.endTime = timeAdapter.endBlock2Str(end);
        }

        @Override
        public int compareTo(TimeCount o) {
            //인원 수 역순
            //날짜 빠른 순
            //시간 빠른 순
            if(this.count == o.count) {
                if(this.availableDate.isEqual(o.availableDate)) {
                    return this.startTime.compareTo(o.endTime);
                } else {
                    return this.availableDate.compareTo(o.availableDate);
                }
            } else {
                return o.count - this.count;
            }
        }
    }

}
