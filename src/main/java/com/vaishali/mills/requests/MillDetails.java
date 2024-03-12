package com.vaishali.mills.requests;

import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;


@Getter

public class MillDetails {


    public String rotorId;

    public String millId;

    public String action;

    public String component;

    public Timestamp startTime;

    @Override
    public String toString() {
        return "MillDetails{" +
                "rotorId='" + rotorId + '\'' +
                ", millId='" + millId + '\'' +
                ", action='" + action + '\'' +
                ", component='" + component + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
