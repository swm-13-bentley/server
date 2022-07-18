package com.schedch.mvp.dto;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantRequestDto {

    @Schema(description = "참가자 이름", example = "sample participantName")
    private String participantName;

    @Schema(description = "참가자 비밀번호", example = "asdf123!")
    private String password;

    public ParticipantRequestDto(String participantName, String password) {
        this.participantName = participantName;
        this.password = password;
    }

    public String toString() {
       return new Gson().toJson(this);
    }
}
