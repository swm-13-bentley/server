package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.room.RoomRequest;
import com.schedch.mvp.mapper.RoomMapper;
import com.schedch.mvp.service.RoomService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@WithMockUser
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean RoomService roomService;
    @MockBean RoomMapper roomMapper;
    @MockBean TimeAdapter timeAdapter;
    private static Gson gson;

    @BeforeAll
    public static void configGson() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }
    @Test
    void 방_생성_테스트() throws Exception {
        //given
        RoomRequest roomRequest = getRoomRequestDto();

        //when
        mockMvc.perform(post("/room")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(gson.toJson(roomRequest))
            )
        //then
                .andExpect(status().isOk());
    }

    @Test
    void 방_생성_필요한_정보_누락_테스트() throws Exception {
        //given
        RoomRequest invalidRoomRequest = getInvalidRoomRequestDto();

        //when
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(gson.toJson(invalidRoomRequest)))
        //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Room title cannot be empty"));
    }

    @Test
    void get_room_info_test() throws Exception {
        //given
        given(roomService.getRoom(any(String.class))).willReturn(null);

        //when
        mockMvc.perform(get("/room/testRoomUuid")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then
                .andExpect(status().isOk());
    }

    @Test
    void get_room_info_failure_test() throws Exception {
        //given
        given(roomService.getRoom(any(String.class)))
                .willThrow(new NoSuchElementException("sampleErrMsg"));

        //when
        mockMvc.perform(get("/room/testRoomUuid")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
        //then
                .andExpect(status().isNotFound()) //404 not found
                .andExpect(jsonPath("message").value("sampleErrMsg")
                );
    }

    private RoomRequest getRoomRequestDto() {
        String title = "test title";
        LocalDate date1 = LocalDate.of(2022, 04, 01);
        LocalDate date2 = LocalDate.of(2022, 04, 02);
        List<LocalDate> dates = Arrays.asList(date1, date2);
        String startTime = "04:30:00";
        String endTime = "24:00:00";
        RoomRequest roomRequest = new RoomRequest(title, dates, startTime, endTime);
        return roomRequest;
    }

    private RoomRequest getInvalidRoomRequestDto() {
        RoomRequest roomRequest = getRoomRequestDto();
        roomRequest.setTitle(null);
        return roomRequest;
    }
}