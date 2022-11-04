package com.schedch.mvp.dto.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessageDto {

    private String message;

    public ErrorMessageDto(String message) {
        this.message = message;
    }
}
