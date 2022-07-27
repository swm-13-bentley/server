package com.schedch.mvp.service;

import com.schedch.mvp.dto.feedback.FeedbackRequest;
import com.schedch.mvp.model.Feedback;
import com.schedch.mvp.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public boolean saveFeedback(FeedbackRequest feedbackRequest) {
        Feedback save = feedbackRepository.save(feedbackRequest.toEntity());
        return save.getContent().equals(feedbackRequest.getContent());
    }
}
