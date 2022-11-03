package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCalendar extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String calendarEmail;

    private boolean mainCalendar;

    private String calendarChannel;
    private String calendarAccessToken;
    private String calendarRefreshToken;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userCalendar")
    private List<SubCalendar> subCalendarList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserCalendar(String calendarEmail, boolean mainCalendar, String calendarChannel, String calendarAccessToken, String calendarRefreshToken) {
        this.calendarEmail = calendarEmail;
        this.mainCalendar = mainCalendar;
        this.calendarChannel = calendarChannel;
        this.calendarAccessToken = calendarAccessToken;
        this.calendarRefreshToken = calendarRefreshToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMainCalendar(boolean mainCalendar) {
        this.mainCalendar = mainCalendar;
    }

    public void addSubCalendar(SubCalendar subCalendar) {
        subCalendarList.add(subCalendar);
        subCalendar.setUserCalendar(this);
    }

    public Set<String> getSelectedSubCalIdSet() {
        return subCalendarList.stream()
                .filter(SubCalendar::isSelected)
                .map(SubCalendar::getGCalId)
                .collect(Collectors.toSet());
    }

    public void setCalendarAccessToken(String calendarAccessToken) {
        this.calendarAccessToken = calendarAccessToken;
    }

    public void setCalendarRefreshToken(String calendarRefreshToken) {
        this.calendarRefreshToken = calendarRefreshToken;
    }
}
