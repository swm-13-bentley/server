package com.schedch.mvp.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoProfileRes {
    private String id;
    private String nickname;
    private String email;
    private String password;
}
