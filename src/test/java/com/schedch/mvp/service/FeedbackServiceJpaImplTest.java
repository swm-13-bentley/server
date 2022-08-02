package com.schedch.mvp.service;

import com.schedch.mvp.dto.feedback.FeedbackRequest;
import com.schedch.mvp.model.Feedback;
import com.schedch.mvp.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceJpaImplTest {

    @InjectMocks private FeedbackServiceJpaImpl feedbackServiceJpaImpl;
    @Mock private FeedbackRepository feedbackRepository;

    @Test
    public void 피드백_저장_테스트() throws Exception {
        //given
        FeedbackRequest feedbackRequest = new FeedbackRequest("기타", "피드백");
        Feedback feedback = feedbackRequest.toEntity();
        given(feedbackRepository.save(any(Feedback.class))).willReturn(feedback);

        //when
        boolean success = feedbackServiceJpaImpl.saveFeedback(feedbackRequest);

        //then
        assertThat(success).isTrue();
    }
}