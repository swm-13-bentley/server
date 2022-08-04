package com.schedch.mvp.integrate;

import com.schedch.mvp.controller.ParticipantController;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import com.schedch.mvp.model.Schedule;
import com.schedch.mvp.repository.ParticipantRepository;
import com.schedch.mvp.service.ParticipantService;
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
public class ParticipantIntegrateTest {

    @Autowired
    ParticipantController participantController;
    @Autowired
    ParticipantService participantService;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 참가자_일정_리셋_테스트() throws Exception {
        //given
        Participant participant = new Participant("test name", "pwd", false);
        participant.addSchedule(new Schedule(LocalDate.of(2022, 4, 1),
                LocalTime.of(4, 30, 0),
                LocalTime.of(6, 0, 0)));

        Participant savedParticipant = participantRepository.save(participant);
        em.flush();
        em.clear();

        //when
        Participant foundParticipant = participantRepository.findById(savedParticipant.getId()).get();
        foundParticipant.emptySchedules();
        foundParticipant.addSchedule(new Schedule(LocalDate.of(2022, 4, 1),
                LocalTime.of(9, 30, 0),
                LocalTime.of(11, 0, 0)));
        em.flush();
        em.clear();

        //then
        Participant foundParticipant2 = participantRepository.findById(foundParticipant.getId()).get();
        assertThat(foundParticipant2.getScheduleList().get(0).getStartTime()).isEqualTo(LocalTime.of(9, 30, 0));

    }

    @Test
    public void 참가자_전원_불러오기() throws Exception {
        //given
        Room room = createRoom();
        Participant participant1 = new Participant("p1", "", false);
        Participant participant2 = new Participant("p2", "", false);
        Participant participant3 = new Participant("p3", "", false);
        room.addParticipant(participant1); room.addParticipant(participant2); room.addParticipant(participant3);

        participant1.addSchedule(new Schedule(LocalDate.of(2022, 2, 2), LocalTime.of(2, 30, 0), LocalTime.of(4, 0, 0)));
        participant1.addSchedule(new Schedule(LocalDate.of(2022, 2, 2), LocalTime.of(6, 30, 0), LocalTime.of(10, 0, 0)));

        participant2.addSchedule(new Schedule(LocalDate.of(2022, 4, 1), LocalTime.of(2, 30, 0), LocalTime.of(10, 0, 0)));

        em.persist(room);

        //when
        List<ParticipantResponseDto> allParticipantsInRoom = participantService.findAllParticipantsInRoom(room.getUuid());

        //then
        assertThat(allParticipantsInRoom.size()).isEqualTo(3);
    }

    private Room createRoom() {
        String title = "testTitle";
        List<RoomDate> roomDates = new ArrayList<>();
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));

        LocalTime startTime = LocalTime.of(16, 00);
        LocalTime endTime   = LocalTime.of(23, 00);

        //when
        return new Room(title, roomDates, startTime, endTime);
    }
}
