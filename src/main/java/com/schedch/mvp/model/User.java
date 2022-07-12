package com.schedch.mvp.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class User {

    @Id @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    private String password;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Participant> participantList = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public void addParticipant(Participant participant) {
        participantList.add(participant);
        participant.setUser(this);
    }
}
