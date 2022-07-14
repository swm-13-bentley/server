package com.schedch.mvp.controller;

import com.schedch.mvp.dto.ParticipantRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.service.ParticipantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantController.class)
class ParticipantControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ParticipantService participantService;

    String roomUuid = "testRoomUuid";
    String participantName = "testName";
    String password = "password";

    @Test
    public void room_participant_password_match_test() throws Exception {
        //given
        ParticipantRequestDto participantRequestDto = new ParticipantRequestDto(participantName, password);
        when(participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password))
                .thenReturn(new ParticipantResponseDto(new Participant(participantName, password, false)));

        //when
        mockMvc.perform(get("/room/testRoomUuid/participant/available")
                .contentType(MediaType.APPLICATION_JSON)
                .param("roomUuid", roomUuid)
                .content(participantRequestDto.toString())
        )
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").hasJsonPath())
                .andExpect(jsonPath("available").hasJsonPath());

    }

    @Test
    public void room_participant_password_mismatch_test() throws Exception {
        //given
        ParticipantRequestDto participantRequestDto = new ParticipantRequestDto(participantName, password);
        when(participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password))
                .thenThrow(new IllegalAccessException("password is wrong"));

        //when
        mockMvc.perform(get("/room/testRoomUuid/participant/available")
                .contentType(MediaType.APPLICATION_JSON)
                .param("roomUuid", roomUuid)
                .content(participantRequestDto.toString())
        )
        //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").hasJsonPath());
    }

    @Test
    public void room_not_found_test() throws Exception {
        //given
        ParticipantRequestDto participantRequestDto = new ParticipantRequestDto(participantName, password);
        when(participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password))
                .thenThrow(new NoSuchElementException("no such element"));

        //when
        mockMvc.perform(get("/room/testRoomUuid/participant/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("roomUuid", roomUuid)
                        .content(participantRequestDto.toString())
                )
        //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").hasJsonPath());
    }
}