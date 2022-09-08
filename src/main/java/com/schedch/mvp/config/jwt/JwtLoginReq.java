package com.schedch.mvp.config.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtLoginReq {
    private String email;
    private String password;
}
