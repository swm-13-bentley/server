package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    @NotNull(message = "username cannot be empty")
    private String username;

    @NotNull(message = "email cannot be empty")
    //oauth email
    private String email;

    @NotNull(message = "password cannot be empty")
    //By default: pwd
    private String password;

    @NotNull(message = "sign in channel cannot be empty")
    //OAuth 진행한 채널 (Google, Kakao 등)
    private String signInChannel;

    @NotNull(message = "user role cannot be empty")
    private String role;

    private String scope;

    private boolean receiveEmail;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<UserCalendar> userCalendarList = new ArrayList();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Participant> participantList = new ArrayList<>();

    public User(String username, String email, String password, String signInChannel, String scope) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.signInChannel = signInChannel;
        this.role = "ROLE_USER";
        this.scope = scope;
        this.receiveEmail = true;
    }
    public void addParticipant(Participant participant) {
        participantList.add(participant);
        participant.setUser(this);
    }

    public void addUserCalendar(UserCalendar userCalendar) {
        userCalendarList.add(userCalendar);
        userCalendar.setUser(this);
    }

    /**
     * 참가자 정보를 삭제한다.
     */
    public void detachFromParticipant() {
        this.participantList.stream()
                .forEach(p -> {
                    p.setUser(null);
                });
    }

    public boolean changeMainCalendarTo(Long newMainCalId) {
        UserCalendar currentMainCal = null;
        UserCalendar newMainCal = null;

        for (UserCalendar userCalendar : userCalendarList) {
            if(userCalendar.isMainCalendar()) {
                currentMainCal = userCalendar;
                continue;
            }

            if (userCalendar.getId().equals(newMainCalId)) {
                newMainCal = userCalendar;
            }
        }

        if(currentMainCal == null || newMainCal == null) {
            return false;
        }

        currentMainCal.setMainCalendar(false);
        newMainCal.setMainCalendar(true);

        return true;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setReceiveEmail(boolean receiveEmail) {
        this.receiveEmail = receiveEmail;
    }
}
