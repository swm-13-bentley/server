package com.schedch.mvp.service;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.repository.RoomRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class RoomServiceTest {

    private static RoomRepository roomRepository;

    @BeforeAll
    static void setUp() {
        roomRepository = Mockito.mock(RoomRepository.class);
    }

    @Test
    public void roomRepository_not_null_test() {
        assertThat(roomRepository).isNotNull();
    }

    @Test
    public void createRoomTest() throws Exception {
        //given
        RoomRequestDto roomRequestDto = createRoomRequestDto();
        Room room = new Room(roomRequestDto);
        when(roomRepository.save(any(Room.class))).thenReturn(new Room(roomRequestDto));

        //when
        Room savedRoom = roomRepository.save(room);

        //then
        assertThat(savedRoom.getUuid()).isNotNull();

    }

    private RoomRequestDto createRoomRequestDto() {
        String title = "test title";
        LocalDate date1 = LocalDate.of(2022, 04, 01);
        LocalDate date2 = LocalDate.of(2022, 04, 02);
        List<LocalDate> dates = Arrays.asList(date1, date2);
//        LocalTime startTime = LocalTime.of(04, 30, 00);
//        LocalTime endTime = LocalTime.of(20, 00, 00);
        String startTime = "04:30:00";
        String endTime = "24:00:00";

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .title(title)
                .dates(dates)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        return roomRequestDto;
    }

}