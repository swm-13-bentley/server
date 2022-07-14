package com.schedch.mvp.integrate;

import com.schedch.mvp.model.Participant;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ParticipantIntegrateTest {

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
}
