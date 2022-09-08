package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.dto.feedback.FeedbackRequest;
import com.schedch.mvp.service.FeedbackServiceJpaImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@WithMockUser
class FeedbackControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean FeedbackServiceJpaImpl feedbackServiceJpaImpl;
    @Autowired Gson gson;

    @Test
    public void 피드백_받기() throws Exception {
        //given
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .type("기타")
                .content("피드백")
                .build();
        given(feedbackServiceJpaImpl.saveFeedback(feedbackRequest)).willReturn(true);

        //when
        mockMvc.perform(post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
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
        given(feedbackServiceJpaImpl.saveFeedback(feedbackRequest)).willReturn(true);

        //when
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(gson.toJson(feedbackRequest))
                )
        //then
                .andExpect(status().isBadRequest());
    }
}