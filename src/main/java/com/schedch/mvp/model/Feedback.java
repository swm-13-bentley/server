package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String type;

    @NotNull
    private String content;

    private String email;

    public Feedback(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public Feedback(String type, String content, String email) {
        this.type = type;
        this.content = content;
        this.email = email;
    }
}
