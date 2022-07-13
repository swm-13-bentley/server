package com.schedch.mvp.dto;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class ParticipantRequestDto {
    private String participantName;
    private String password;

    public ParticipantRequestDto(String participantName, String password) {
        this.participantName = participantName;
        this.password = password;
    }

    public String toString() {
       return new Gson().toJson(this);
    }
}
