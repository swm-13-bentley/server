package com.schedch.mvp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor
public class User {

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

    private String calendarChannel;
    private String calendarAccessToken;
    private String calendarRefreshToken;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Participant> participantList = new ArrayList<>();

    public User(String username, String email, String password, String signInChannel) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.signInChannel = signInChannel;
        this.role = "ROLE_USER";
    }

    @Builder
    public User(String username, String email, String password, String signInChannel, String calendarChannel, String calendarAccessToken, String calendarRefreshToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.signInChannel = signInChannel;
        this.calendarChannel = calendarChannel;
        this.calendarAccessToken = calendarAccessToken;
        this.calendarRefreshToken = calendarRefreshToken;
        this.role = "ROLE_USER";
    }

    public void addParticipant(Participant participant) {
        participantList.add(participant);
        participant.setUser(this);
    }
}
