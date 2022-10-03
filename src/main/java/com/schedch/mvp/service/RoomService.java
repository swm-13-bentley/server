package com.schedch.mvp.service;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.model.*;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    public String createRoom(Room room) {
        room.setParticipantLimit(50);
        Room save = roomRepository.save(room);
        return save.getUuid();
    }

    public String createPremiumRoom(Room room) {
        room.setParticipantLimit(50);
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

    public Room getRoomWithParticipants(String roomUuid) {
        Optional<Room> roomOptional = roomRepository.findByUuidJoinFetchParticipant(roomUuid);
        Room room = roomOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid))
        );
        return room;
    }

    public Room getRoomWithRoomDates(String roomUuid) {
        Optional<Room> roomOptional = roomRepository.findByUuidJoinFetchRoomDates(roomUuid);
        Room room = roomOptional.orElseThrow(
                () -> new NoSuchElementException(String.format("Room for uuid: %s not found", roomUuid))
        );
        return room;
    }

    public List<TopTime> getTopAvailableTimeAndNames(String roomUuid, int max) {
        Room room = getRoom(roomUuid);
        List<RoomDate> roomDates = room.getRoomDates();

        //get room start time block
        LocalTime roomStartTime = room.getStartTime();
        int roomStartTimeBlock = TimeAdapter.localTime2TimeBlockInt(roomStartTime);

        //get room end time block, add 48 if end time is before start time (night times)
        LocalTime roomEndTime = room.getEndTime();
        int roomEndTimeBlock = TimeAdapter.localTime2TimeBlockInt(roomEndTime);
        if (roomEndTime.isBefore(roomStartTime)) {
            roomEndTimeBlock += 48;
        }

        // initialize column map
        HashMap<LocalDate, Integer> columnMap = getColumnMap(roomDates);

        // create 2d hash set arr list and initialize
        int rowSize = roomEndTimeBlock - roomStartTimeBlock + 1;
        int colSize = roomDates.size();
        int[][] sizeBoard = new int[rowSize + 1][colSize];
        long[][] bitBoard = new long[rowSize][colSize];

        // fill board by participant schedules, O(p x d x t) [p: participants, d: dates, t: times]
        List<Participant> participantList = participantRepository.findAllByRoom(room);
        fillBoard(participantList, roomStartTimeBlock, columnMap, sizeBoard, bitBoard);

        // init max Counter
        List<TopTime> topTimeList = new ArrayList<>();

        // check top list, O(p x d x t)
        for (int j = 0; j < colSize; j++) {
            LocalDate availableDate = roomDates.get(j).getScheduledDate();
            Stack<TopTime> stack = new Stack<>();

            for (int i = 0; i < rowSize; i++) {
                int nowStartBlock = i + roomStartTimeBlock;
                int nowSize = sizeBoard[i][j];
                long nowBit = bitBoard[i][j];

                if (stack.isEmpty()) {
                    stack.add(new TopTime(availableDate, nowSize, nowBit, nowStartBlock, 1));
                    continue;
                }

                adjustStack(stack, topTimeList, availableDate, nowStartBlock, nowSize, nowBit);
            }

            while (!stack.isEmpty()) {
                //최상단 꺼내기
                TopTime popNode = stack.pop();

                //사람 수 >= 1이면 추가
                if (popNode.getParticipantSize() >= 1) {
                    topTimeList.add(popNode);
                }

                if (!stack.isEmpty()) {
                    stack.peek().addLen(popNode.getLen());
                } else {
                    break;
                }
            }
        }

        Collections.sort(topTimeList);
        List<TopTime> topTimes = topTimeList.subList(0, Math.min(topTimeList.size(), max));
        topTimes.stream().forEach(topTime -> {
            long participantBit = topTime.getParticipantBit();
            for (int i = 0; i < participantList.size(); i++) {
                if((participantBit & (1 << i)) > 0) {
                    topTime.addName(participantList.get(i).getParticipantName());
                }
            }
        });

        return topTimes;
    }

    private void adjustStack(Stack<TopTime> stack, List<TopTime> topTimeList, LocalDate availableDate, int nowStartBlock, int nowSize, long nowBit) {
        TopTime peekNode = stack.peek();

        if (nowBit == peekNode.getParticipantBit()) {//구성원이 같다면 len 추가
            peekNode.addLen(1);
        } else if (checkConsist(peekNode, nowSize, nowBit) == true) {//인원 포함 + 추가된다면, stack에 새 항목 추가
            stack.add(new TopTime(availableDate, nowSize, nowBit, nowStartBlock, 1));

        } else { //인원 구성 아예 변동됨 -> stack 구조 변경
            while (!stack.isEmpty()) {
                //최상단 꺼내기
                TopTime popNode = stack.pop(); // = peekNode

                //사람 수 >= 1이면 추가
                if (popNode.getParticipantSize() >= 1) {
                    topTimeList.add(popNode);
                }

                if (!stack.isEmpty()) { // stack에 항목이 남아있다면
                    TopTime peekAfterPop = stack.peek();
                    peekAfterPop.addLen(popNode.getLen());

                    if (peekAfterPop.getParticipantBit() == nowBit) { // bit가 같다면 길이 + 1
                        peekAfterPop.addLen(1);
                        break;
                    }

                    //포함 관계라면 stack에 추가
                    else if (checkConsist(stack.peek(), nowSize, nowBit)) {//포함 관계
                        stack.add(new TopTime(availableDate, nowSize, nowBit, nowStartBlock, 1));
                        break;
                    } else { //peekAfterPop도 제거 해야 할 대상임
                        continue;
                    }
                }
            }

            if (stack.isEmpty()) {//stack이 빈 상태가 되어버렸다면
                stack.add(new TopTime(availableDate, nowSize, nowBit, nowStartBlock, 1));
            }
        }
    }

    private boolean checkConsist(TopTime topTime, int size, long bit) {
        if (topTime.getParticipantSize() <= size
                && (topTime.getParticipantBit() <= (topTime.getParticipantBit() & bit))) return true;
        else return false;
    }

    private void fillBoard(List<Participant> participantList, int roomStartTimeBlock, HashMap<LocalDate, Integer> columnMap, int[][] sizeBoard, long[][] bitBoard) {
        for (int pIdx = 0; pIdx < participantList.size(); pIdx++) {
            Participant participant = participantList.get(pIdx);
            List<Schedule> scheduleList = participant.getScheduleList();

            for (Schedule schedule : scheduleList) {
                LocalDate availableDate = schedule.getAvailableDate();
                if (!columnMap.containsKey(availableDate)) {
                    log.warn("참가자가 입력한 날짜가 방의 날짜 기간을 벗어납니다.");
                    continue;
                }

                int startBlock = TimeAdapter.localTime2TimeBlockInt(schedule.getStartTime());
                if (startBlock < roomStartTimeBlock) startBlock += 48; // 시작 시간이 새벽 시간대라면, +48

                int endBlock = TimeAdapter.localTime2TimeBlockInt(schedule.getEndTime());
                if (endBlock < roomStartTimeBlock) endBlock += 48; // 시작 시간이 새벽 시간대라면, +48

                int colIdx = columnMap.get(availableDate);

                // size board 구간합 +- 적용
                sizeBoard[startBlock - roomStartTimeBlock][colIdx]++;
                sizeBoard[endBlock - roomStartTimeBlock + 1][colIdx]--;

                // bit board 업데이트
                for (int rowIdx = startBlock; rowIdx <= endBlock; rowIdx++) {
                    bitBoard[rowIdx - roomStartTimeBlock][colIdx] |= (1L << pIdx);
                }
            }
        }

        //구간 합 업데이트
        for (int j = 0; j < sizeBoard[0].length; j++) {
            for (int i = 1; i < sizeBoard.length; i++) {
                sizeBoard[i][j] += sizeBoard[i - 1][j];
            }
        }
    }

    private HashMap<LocalDate, Integer> getColumnMap(List<RoomDate> roomDates) {
        int colNum = 0;
        HashMap<LocalDate, Integer> colNumMap = new HashMap<>();
        for (RoomDate roomdate : roomDates) {
            colNumMap.put(roomdate.getScheduledDate(), colNum++);
        }
        return colNumMap;
    }
}
