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

    @Builder
    public FeedbackRequest(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public Feedback toEntity() {
        return new Feedback(type, content);
    }
}
