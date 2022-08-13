package com.schedch.mvp.dto;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantRequestDto {

    private String participantName;
    private String password;

    public ParticipantRequestDto(String participantName, String password) {
        this.participantName = participantName;
        this.password = password;
    }
}
