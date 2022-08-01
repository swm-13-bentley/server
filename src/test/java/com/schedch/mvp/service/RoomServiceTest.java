package com.schedch.mvp.service;

import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @InjectMocks RoomService roomService;
    @Mock RoomRepository roomRepository;

    @Test
    public void createRoomTest() throws Exception {
        //given
        Room room = getRoom();
        given(roomRepository.save(any(Room.class))).willReturn(room);

        //when
        String uuid = roomService.createRoom(room);

        //then
        assertThat(uuid).isNotNull();

    }

    private Room getRoom() {
        String title = "test title";
        LocalTime startTime = LocalTime.of(4, 30, 0);
        LocalTime endTime = LocalTime.of(23, 0, 0);
        List<RoomDate> roomDateList = new ArrayList<>();
        roomDateList.add(new RoomDate(LocalDate.of(2022, 04, 01)));
        roomDateList.add(new RoomDate(LocalDate.of(2022, 04, 02)));

        Room room = new Room(title, roomDateList, startTime, endTime);

        return room;
    }

}