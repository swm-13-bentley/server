package com.schedch.mvp.dto.user.username;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsernamePatchRes {

    private String newJwtToken;

    public UsernamePatchRes(String newJwtToken) {
        this.newJwtToken = newJwtToken;
    }
}
