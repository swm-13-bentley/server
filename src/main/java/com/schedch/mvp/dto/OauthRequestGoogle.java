package com.schedch.mvp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OauthRequestGoogle {
    private String email;
    private String code;
}
