package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCalendar {

    @Id
    @GeneratedValue
    private Long id;

    private String subCalendarName;

    //google calendar id
    private String gCalId;

    private boolean selected;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_calendar_id")
    private UserCalendar userCalendar;

    public SubCalendar(String subCalendarName, boolean selected, String gSubCalId) {
        this.subCalendarName = subCalendarName;
        this.gCalId = gSubCalId;
        this.selected = selected;
    }

    public void setUserCalendar(UserCalendar userCalendar) {
        this.userCalendar = userCalendar;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
