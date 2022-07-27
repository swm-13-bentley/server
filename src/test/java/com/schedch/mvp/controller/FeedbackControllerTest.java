package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.dto.feedback.FeedbackRequest;
import com.schedch.mvp.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean FeedbackService feedbackService;
    @Autowired Gson gson;

    @Test
    public void 피드백_받기() throws Exception {
        //given
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .type("기타")
                .content("피드백")
                .build();
        given(feedbackService.saveFeedback(feedbackRequest)).willReturn(true);

        //when
        mockMvc.perform(post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(feedbackRequest))
        )
        //then
                .andExpect(status().isOk());
    }

    @Test
    public void 피드백_타입_부재_오류() throws Exception {
        //given
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .type("기타")
                .build();
        given(feedbackService.saveFeedback(feedbackRequest)).willReturn(true);

        //when
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(feedbackRequest))
                )
        //then
                .andExpect(status().isBadRequest());
    }
}