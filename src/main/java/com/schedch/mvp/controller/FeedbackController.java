package com.schedch.mvp.controller;

import com.google.gson.Gson;
import com.schedch.mvp.config.NotionConfig;
import com.schedch.mvp.dto.feedback.FeedbackRequest;
import com.schedch.mvp.service.FeedbackService;
import com.schedch.mvp.service.FeedbackServiceJpaImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ResponseEntity postFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        System.out.println("requested");
        feedbackService.saveFeedback(feedbackRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
