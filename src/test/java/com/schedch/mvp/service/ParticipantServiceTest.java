package com.schedch.mvp.service;

import com.google.gson.Gson;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.participant.ParticipantRes;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.exception.FullMemberException;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(TimeAdapter.class)
class ParticipantServiceTest {

    @InjectMocks
    ParticipantService participantService;
    @Mock
    ParticipantRepository participantRepository;
    @Mock
    RoomService roomService;


    String roomUuid = "testRoomUuid";
    String participantName = "testName";
    String password = "testPwd";

    @Test
    void no_room_for_uuid_test() throws Exception {
        //given
        when(roomService.getRoom(roomUuid))
                .thenThrow(new NoSuchElementException());

        //when
        try {
            participantService.getParticipant(roomUuid, participantName, password);
        }
        //then
        catch (NoSuchElementException e) {
            return;
        }
        Assertions.fail("cannot reach here");
    }

    @Test
    void new_user_registration_test() throws Exception {
        //given
        when(roomService.getRoom(roomUuid))
                .thenReturn(createTestRoom());

        //when
        Participant participant = participantService.getParticipant(roomUuid, participantName, password);

        //then
        assertThat(participant.getParticipantName()).isEqualTo(participantName);
        assertThat(participant.getScheduleList().isEmpty()).isTrue();
    }

    @Test
    void user_password_mismatch_test() throws Exception {
        //given
        Participant participant = new Participant(participantName, password, false);
        Room room = createTestRoom();
        room.addParticipant(participant);

        given(roomService.getRoom(roomUuid))
                .willReturn(room);
        given(participantRepository.findParticipantByRoomAndParticipantNameWithSchedules(room, participantName))
                .willReturn(List.of(participant));

        //when
        try {
            participantService.getParticipant(roomUuid, participantName, "wrongPassword");
        }
        //then
        catch (IllegalAccessException e) {
            return;
        }
        Assertions.fail("cannot reach here");

    }

    @Test
    void user_password_match_test() throws Exception {
        //given
        Participant participant = new Participant(participantName, password, false);
        participant.addSchedule(new Schedule(
                LocalDate.of(2022, 4, 1),
                LocalTime.of(4, 30, 0),
                LocalTime.of(6, 0, 0)));
        Room room = createTestRoom();
        room.addParticipant(participant);
        given(roomService.getRoom(roomUuid)).willReturn(room);
        given(participantRepository.findParticipantByRoomAndParticipantNameWithSchedules(room, participantName))
                .willReturn(List.of(participant));

        //when
        Participant foundParticipant = participantService.getParticipant(roomUuid, participantName, password);

        //then
        assertThat(foundParticipant.getParticipantName()).isEqualTo(participantName);
        assertThat(foundParticipant.getScheduleList().size()).isGreaterThan(0);

    }

    @Test
    void 약속_인원_제한_테스트() throws Exception {
        //given
        Room room = createTestRoom();
        room.setParticipantLimit(5);
        given(roomService.getRoom(roomUuid)).willReturn(room);

        for (int i = 1; i <= 5; i++) {
            room.addParticipant(new Participant("p" + i, "", false));
        }

        //when
        try {
            participantService.getParticipant(roomUuid, "p6", "");
        }

        //then
        catch (FullMemberException e){
            return;
        }

        Assertions.fail("should not reach here");
    }

    @Test
    public void 유저_입력_시간_스케쥴_변환_테스트() throws Exception {
        //given
        TimeBlockDto timeBlockDto1 = new TimeBlockDto(
                LocalDate.of(2022, 4, 1),
                Arrays.asList(1, 2, 3, 10, 11, 12, 20)
        );

        TimeBlockDto timeBlockDto2 = new TimeBlockDto(
                LocalDate.of(2022, 4, 1),
                Arrays.asList(1)
        );

        TimeBlockDto timeBlockDto3 = new TimeBlockDto(
                LocalDate.of(2022, 4, 1),
                Arrays.asList()
        );

        LocalTime roomStartTime = LocalTime.of(0, 0);

        //when
        List<Schedule> scheduleList1 = TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto1, roomStartTime);
        List<Schedule> scheduleList2 = TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto2, roomStartTime);
        List<Schedule> scheduleList3 = TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto3, roomStartTime);

        //then
        assertThat(scheduleList1.size()).isEqualTo(3);
        assertThat(scheduleList1.get(2).getEndTime()).isEqualTo(LocalTime.of(10, 29, 0));
        assertThat(scheduleList2.size()).isEqualTo(1);
        assertThat(scheduleList3.size()).isEqualTo(0);
    }

    @Test
    public void 새벽시간_스케쥴_입력_테스트() throws Exception {
        //given
        TimeBlockDto timeBlockDto = new TimeBlockDto(
                LocalDate.of(2022, 4, 1),
                Arrays.asList(42, 43, 44, 45, 46, 47, 48, 49, 50, 56, 57, 58) //21시 - 1시 29분, 4시 - 5시 29분
        );

        //방 시작 시간: 8시
        LocalTime roomStartTime = LocalTime.of(20, 0);

        //when
        List<Schedule> scheduleList = TimeAdapter.changeTimeBlockDtoToSchedule(timeBlockDto, roomStartTime);

        //then
        assertThat(scheduleList.size()).isEqualTo(2);
        assertThat(scheduleList.get(0).getStartTime()).isEqualTo(LocalTime.of(21, 0));
        assertThat(scheduleList.get(0).getEndTime()).isEqualTo(LocalTime.of(1, 29));
        assertThat(scheduleList.get(1).getStartTime()).isEqualTo(LocalTime.of(4, 0));
        assertThat(scheduleList.get(1).getEndTime()).isEqualTo(LocalTime.of(5, 29));
    }

    @Test
    public void 새벽시간_스케줄_반환_테스트() throws Exception {
        //given
        Participant participant = new Participant("some name", "pwd", false);

        LocalTime roomStartTime = LocalTime.of(20, 0); //방 시작 시간: 8시
        LocalDate availableDate = LocalDate.of(2022, 8, 10);
        participant.addSchedule(new Schedule(availableDate, LocalTime.of(21, 0), LocalTime.of(21, 59), roomStartTime)); //42 ~ 43, 2
        participant.addSchedule(new Schedule(availableDate, LocalTime.of(23, 0), LocalTime.of(4, 29), roomStartTime)); //46 ~ 56, 11
        participant.addSchedule(new Schedule(availableDate, LocalTime.of(8, 0), LocalTime.of(8, 59), roomStartTime)); //64 ~ 65, 2

        //when
        ParticipantRes res = new ParticipantRes(participant);

        //then
        assertThat(res.getAvailable().size()).isEqualTo(1);

        List<Integer> availableTimeList = res.getAvailable().get(0).getAvailableTimeList();
        assertThat(availableTimeList.size()).isEqualTo(15);
        assertThat(new Gson().toJson(availableTimeList)).isEqualTo("[42,43,46,47,48,49,50,51,52,53,54,55,56,64,65]");
    }

    private Room createTestRoom() {
        String title = "test title";
        LocalTime startTime = LocalTime.of(4, 30, 0);
        LocalTime endTime = LocalTime.of(23, 0, 0);
        List<RoomDate> roomDateList = new ArrayList<>();
        roomDateList.add(new RoomDate(LocalDate.of(2022, 04, 01)));
        roomDateList.add(new RoomDate(LocalDate.of(2022, 04, 02)));

        Room room = new Room(title, roomDateList, startTime, endTime);
        room.setParticipantLimit(5);

        return room;
    }
}