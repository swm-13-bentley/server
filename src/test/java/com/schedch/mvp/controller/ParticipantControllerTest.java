package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.dto.AvailableRequestDto;
import com.schedch.mvp.dto.ParticipantRequestDto;
import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.exception.FullMemberException;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.service.ParticipantService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantController.class)
@WithMockUser
class ParticipantControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ParticipantService participantService;
    @Autowired Gson gson;
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
        mockMvc.perform(post("/room/testRoomUuid/participant/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("roomUuid", roomUuid)
                .content(gson.toJson(participantRequestDto))
        )
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("participantName").hasJsonPath())
                .andExpect(jsonPath("available").hasJsonPath());

    }

    @Test
    public void room_participant_password_mismatch_test() throws Exception {
        //given
        ParticipantRequestDto participantRequestDto = new ParticipantRequestDto(participantName, password);
        when(participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password))
                .thenThrow(new IllegalAccessException("password is wrong"));

        //when
        mockMvc.perform(post("/room/testRoomUuid/participant/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("roomUuid", roomUuid)
                .content(gson.toJson(participantRequestDto))
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
        mockMvc.perform(post("/room/testRoomUuid/participant/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("roomUuid", roomUuid)
                        .content(gson.toJson(participantRequestDto))
                )
        //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").hasJsonPath());
    }
    @Test
    public void 인원_제한_예외처리_테스트() throws Exception {
        //given
        ParticipantRequestDto participantRequestDto = new ParticipantRequestDto(participantName, password);
        given(participantService.findUnSignedParticipantAndValidate(roomUuid, participantName, password))
                .willThrow(new FullMemberException("room is full"));

        //when
        mockMvc.perform(post("/room/testRoomUuid/participant/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("roomUuid", roomUuid)
                .content(gson.toJson(participantRequestDto))
        )
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 유저_시간_입력받기() throws Exception {
        //given
        AvailableRequestDto availableRequestDto = new AvailableRequestDto();

        //when
        mockMvc.perform(post("/room/testRoomUuid/participant/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(gson.toJson(availableRequestDto))
                )
        //then
                .andExpect(status().isOk());
    }


}