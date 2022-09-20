package com.schedch.mvp.model;


import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TopTime implements Comparable<TopTime> {
    LocalDate availableDate;
    int participantSize;
    long participantBit;
    int start;
    int len;
    List<String> participantNames = new ArrayList<>();

    public TopTime(LocalDate availableDate, int participantSize, long participantBit, int start, int len) {
        this.availableDate = availableDate;
        this.participantSize = participantSize;
        this.participantBit = participantBit;
        this.start = start;
        this.len = len;
    }

    public void addLen(int num) {
        this.len += num;
    }

    public void addName(String name) {
        this.participantNames.add(name);
    }

    @Override
    public int compareTo(TopTime o) {
        if (this.participantSize == o.participantSize) {
            if (o.len == this.len) {
                if (this.availableDate.isEqual(o.availableDate)) {
                    return this.start - o.start;
                } else {
                    return this.availableDate.compareTo(o.availableDate);
                }
            } else {
                return o.len - this.len;
            }
        } else {
            return o.participantSize - this.participantSize;
        }
    }
}
