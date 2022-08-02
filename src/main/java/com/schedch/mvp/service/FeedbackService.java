package com.schedch.mvp.service;

import com.schedch.mvp.dto.feedback.FeedbackRequest;
import org.springframework.stereotype.Service;

public interface FeedbackService {
    public boolean saveFeedback(FeedbackRequest feedbackRequest);
}
