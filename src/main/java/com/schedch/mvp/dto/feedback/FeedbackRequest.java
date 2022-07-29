package com.schedch.mvp.dto.feedback;

import com.schedch.mvp.model.Feedback;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class FeedbackRequest {

    @NotNull
    private String type;

    @NotNull
    private String content;

    private String email;

    @Builder
    public FeedbackRequest(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public FeedbackRequest(String type, String content, String email) {
        this.type = type;
        this.content = content;
        this.email = email;
    }

    public Feedback toEntity() {
        if(email == null) {
            return new Feedback(type, content);
        } else {
            return new Feedback(type, content, email);
        }
    }
}
