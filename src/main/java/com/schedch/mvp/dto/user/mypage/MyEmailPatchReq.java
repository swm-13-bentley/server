package com.schedch.mvp.dto.user.mypage;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyEmailPatchReq {

    private String email;
    private boolean receiveEmail;

}
