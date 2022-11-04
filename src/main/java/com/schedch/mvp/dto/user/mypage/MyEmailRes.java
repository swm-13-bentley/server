package com.schedch.mvp.dto.user.mypage;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyEmailRes {

    private String email;
    private boolean receiveEmail;

    public MyEmailRes(String email, boolean receiveEmail) {
        this.email = email;
        this.receiveEmail = receiveEmail;
    }
}
