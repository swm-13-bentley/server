package com.schedch.mvp.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParticipantTest {
    
    @Test
    public void create_participant_test() throws Exception {
        //given
        String participantName = "testName";
        String password = "testPwd";

        //when
        try {
            Participant participant = new Participant(participantName, password, false);
        }
        //then
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}