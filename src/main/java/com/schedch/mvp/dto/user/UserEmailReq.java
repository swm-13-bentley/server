package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UserEmailReq {

    @Email
    @NotNull
    private String alarmEmail;
}
