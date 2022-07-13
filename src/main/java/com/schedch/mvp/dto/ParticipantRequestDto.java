package com.schedch.mvp.dto;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class ParticipantRequestDto {
    private String username;
    private String password;

    public ParticipantRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String toString() {
       return new Gson().toJson(this);
    }
}
