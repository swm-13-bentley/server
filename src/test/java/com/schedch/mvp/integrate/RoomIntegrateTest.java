package com.schedch.mvp.integrate;

import com.schedch.mvp.controller.RoomController;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.model.Schedule;
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
import static org.mockito.ArgumentMatchers.any;

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

    @Test
    public void 방_TOP_N_테스트() throws Exception {
        //given
        String title = "test title";
        LocalTime startTime = LocalTime.of(8, 0, 0); //16
        LocalTime endTime = LocalTime.of(11, 59, 0); //24 -> 23
        List<RoomDate> roomDateList = new ArrayList<>();
        roomDateList.add(new RoomDate(LocalDate.of(2022, 04, 01)));

        Room room = new Room(title, roomDateList, startTime, endTime);

        Participant participant1 = new Participant("u1", "", false);
        Participant participant2 = new Participant("u2", "", false);
        Participant participant3 = new Participant("u3", "", false);
        Participant participant4 = new Participant("u4", "", false);
        participant1.addSchedule(new Schedule(LocalDate.of(2022, 04, 01), LocalTime.of(8, 0, 0), LocalTime.of(10, 29, 0)));
        participant2.addSchedule(new Schedule(LocalDate.of(2022, 04, 01), LocalTime.of(8, 0, 0), LocalTime.of(11, 29, 0)));
        participant3.addSchedule(new Schedule(LocalDate.of(2022, 04, 01), LocalTime.of(8, 0, 0), LocalTime.of(10, 59, 0)));
        participant3.addSchedule(new Schedule(LocalDate.of(2022, 04, 01), LocalTime.of(11, 30, 0), LocalTime.of(11, 59, 0)));
        participant4.addSchedule(new Schedule(LocalDate.of(2022, 04, 01), LocalTime.of(8, 30, 0), LocalTime.of(9, 29, 0)));
        participant4.addSchedule(new Schedule(LocalDate.of(2022, 04, 01), LocalTime.of(10, 0, 0), LocalTime.of(10, 29, 0)));

        room.addParticipant(participant1);
        room.addParticipant(participant2);
        room.addParticipant(participant3);
        room.addParticipant(participant4);

        //when
        em.persist(room);
        List<RoomService.TopTime> topAvailableTimeAndNames = roomService.getTopAvailableTimeAndNames(room.getUuid(), 5);

        //then
        assertThat(topAvailableTimeAndNames.get(0).getStart()).isEqualTo(17);
        assertThat(topAvailableTimeAndNames.get(0).getLen()).isEqualTo(2);
        assertThat(topAvailableTimeAndNames.get(0).getParticipantSize()).isEqualTo(4);

        assertThat(topAvailableTimeAndNames.get(2).getStart()).isEqualTo(16);
        assertThat(topAvailableTimeAndNames.get(2).getLen()).isEqualTo(5);
        assertThat(topAvailableTimeAndNames.get(2).getParticipantSize()).isEqualTo(3);

    }
}
