package com.schedch.mvp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AvailableRequestDto {

    private String participantName;

    private List<TimeBlockDto> available = new ArrayList<>();
}
