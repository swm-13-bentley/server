package com.schedch.mvp.dto.email;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class EmailReq {
    private boolean dateOnly;
    private String emailTitle;
    private String roomTitle;
    private String roomLink;
    private String attendeeName;
    private String mailTo;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private String sequence;
    private String uid;
    private String summary;

    @Builder
    public EmailReq(boolean dateOnly, String emailTitle, String roomTitle, String roomLink, String attendeeName, String mailTo, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String sequence, String uid, String summary) {
        this.dateOnly = dateOnly;
        this.emailTitle = emailTitle;
        this.roomTitle = roomTitle;
        this.roomLink = roomLink;
        this.attendeeName = attendeeName;
        this.mailTo = mailTo;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.sequence = sequence;
        this.uid = uid;
        this.summary = summary;
    }

    public String getStartDateTimeString() {
        return startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replace("-", "")
                .replace(":", "");
    }

    public String getEndDateTimeString() {
        return endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replace("-", "")
                .replace(":", "");
    }
}