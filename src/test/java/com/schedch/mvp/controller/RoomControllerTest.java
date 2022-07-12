package com.schedch.mvp.controller;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.service.RoomService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean RoomService roomService;

    @Test
    void create_room_test() throws Exception {
        //given
        RoomRequestDto roomRequestDto = getRoomRequestDto();

        //when

        //then
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomRequestDto.toString())
            )
                .andExpect(status().isOk());
    }

    @Test
    void create_room_invalid_argument_test() throws Exception {
        //given
        RoomRequestDto invalidRoomRequestDto = getInvalidRoomRequestDto();

        //when

        //then
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRoomRequestDto.toString())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Room title cannot be empty")
                );




    }

//    @Test
//    void room_get_test() throws Exception {
//        //given
//    }

    private RoomRequestDto getRoomRequestDto() {
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
        return roomRequestDto;
    }

    private RoomRequestDto getInvalidRoomRequestDto() {
        RoomRequestDto roomRequestDto = getRoomRequestDto();
        roomRequestDto.setTitle(null);
        return roomRequestDto;
    }
}