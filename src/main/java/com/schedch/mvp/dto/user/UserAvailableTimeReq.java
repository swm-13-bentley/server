package com.schedch.mvp.dto.user;

import com.schedch.mvp.dto.TimeBlockDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserAvailableTimeReq {

    private List<TimeBlockDto> available;
}
