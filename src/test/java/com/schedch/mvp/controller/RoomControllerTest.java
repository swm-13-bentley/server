package com.schedch.mvp.controller;

import com.schedch.mvp.dto.RoomRequestDto;
import com.schedch.mvp.dto.RoomResponseDto;
import com.schedch.mvp.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
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
        when(roomService.createRoom(roomRequestDto)).thenReturn("roomUuid");

        //when
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roomRequestDto.toString())
            )
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("roomUuid")).hasJsonPath());
    }

    @Test
    void create_room_invalid_argument_test() throws Exception {
        //given
        RoomRequestDto invalidRoomRequestDto = getInvalidRoomRequestDto();

        //when
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRoomRequestDto.toString()))
        //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Room title cannot be empty"));
    }

    @Test
    void get_room_info_test() throws Exception {
        //given
        RoomResponseDto roomResponseDto = RoomResponseDto.builder()
                .title("sample title")
                .dates(Arrays.asList(LocalDate.of(2022, 4, 1), LocalDate.of(2022, 4, 2)))
//                .startTime(LocalTime.of(4, 30, 0))
//                .endTime(LocalTime.of(20, 0, 0))
                .startTime(9)
                .endTime(40)
                .build();
        when(roomService.getRoomInfo("testRoomUuid")).thenReturn(roomResponseDto);

        //when
        mockMvc.perform(get("/room/testRoomUuid"))
        //then
                .andExpect(status().isOk());
    }

    @Test
    void get_room_info_failure_test() throws Exception {
        //given
        String errorMessage = "Room for uuid: testRoomUuid not found";
        when(roomService.getRoomInfo("testRoomUuid"))
                .thenThrow(new NoSuchElementException(errorMessage));

        //when
        mockMvc.perform(get("/room/testRoomUuid"))
        //then
                .andExpect(status().isNotFound()) //404 not found
                .andExpect(jsonPath("message").value(errorMessage)
                );
    }

    private RoomRequestDto getRoomRequestDto() {
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

    private RoomRequestDto getInvalidRoomRequestDto() {
        RoomRequestDto roomRequestDto = getRoomRequestDto();
        roomRequestDto.setTitle(null);
        return roomRequestDto;
    }
}