package com.schedch.mvp.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor @AllArgsConstructor
public class GCalTokenRequest {

    @NotNull(message = "roomUuid is empty")
    private String roomUuid;
    @NotNull(message = "participantName is empty")
    private String participantName;
}
