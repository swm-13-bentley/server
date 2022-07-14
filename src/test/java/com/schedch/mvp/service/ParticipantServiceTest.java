package com.schedch.mvp.service;

import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.RoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@WebMvcTest(ParticipantService.class)
class ParticipantServiceTest {

    @Autowired ParticipantService participantService;
    @MockBean RoomRepository roomRepository;
    String roomUuid = "testRoomUuid";
    String participantName = "testName";
    String password = "testPwd";

    @Test
    void no_room_for_uuid_test() throws Exception {
        //given
        when(roomRepository.findByUuid(roomUuid))
                .thenReturn(Optional.empty());

        //when
        try {
            participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password);
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
        when(roomRepository.findByUuid(roomUuid))
                .thenReturn(Optional.of(createRoom()));

        //when
        ParticipantResponseDto participantResponseDto = participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password);

        //then
        assertThat(participantResponseDto.getParticipantName()).isEqualTo(participantName);
        assertThat(participantResponseDto.getTimeBlockDtoList().isEmpty()).isTrue();
    }

    @Test
    void user_password_mismatch_test() throws Exception {
        //given
        Participant participant = new Participant(participantName, password, false);
        Room room = createRoom();

        room.addParticipant(participant);
        when(roomRepository.findByUuid(roomUuid))
                .thenReturn(Optional.of(room));

        //when
        try {
            participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, "wrongPassword");
        }
        //then
        catch (IllegalAccessException e) {
            return;
        }
        Assertions.fail("cannot reach here");

    }

    @Test
    void user_password_match_test() throws Exception {
        Participant participant = new Participant(participantName, password, false);
        participant.addSchedule(new Schedule(
                LocalDate.of(2022, 4, 1),
                LocalTime.of(4, 30, 0),
                LocalTime.of(6, 0, 0)));
        Room room = createRoom();

        room.addParticipant(participant);
        when(roomRepository.findByUuid(roomUuid))
                .thenReturn(Optional.of(room));

        ParticipantResponseDto participantResponseDto = participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password);

        //then
        assertThat(participantResponseDto.getParticipantName()).isEqualTo(participantName);
        assertThat(participantResponseDto.getTimeBlockDtoList().size()).isGreaterThan(0);

    }

    private Room createRoom() {
        String title = "test title";
        LocalDate date1 = LocalDate.of(2022, 04, 01);
        LocalDate date2 = LocalDate.of(2022, 04, 02);
        List<LocalDate> dates = Arrays.asList(date1, date2);
        LocalTime startTime = LocalTime.of(04, 30, 00);
        LocalTime endTime = LocalTime.of(20, 00, 00);

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .title(title)
                .dates(dates)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        return new Room(roomRequestDto);
    }
}