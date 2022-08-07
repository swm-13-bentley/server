package com.schedch.mvp.integrate;

import com.schedch.mvp.controller.RoomController;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.repository.RoomRepository;
import com.schedch.mvp.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RoomIntegrateTest {

    @Autowired
    RoomController roomController;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 방_날짜_입력_테스트() throws Exception {
        //given
        String title = "testTitle";
        List<RoomDate> roomDates = new ArrayList<>();
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));

        LocalTime startTime = LocalTime.of(16, 00);
        LocalTime endTime   = LocalTime.of(23, 00);

        //when
        Room room = new Room(title, roomDates, startTime, endTime);
        String uuid = roomService.createRoom(room);
        em.flush();

        //then
        Room foundRoom = roomService.getRoom(uuid);
        assertThat(foundRoom.getCreateDate()).isNotNull();
        assertThat(foundRoom.getRoomDates().size()).isEqualTo(3);
    }
}
