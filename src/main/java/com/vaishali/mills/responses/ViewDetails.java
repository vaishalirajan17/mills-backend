package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
public class ViewDetails {

    private String millId;
    private String rotorId;
    private int fdpHours;
    private Timestamp fdpDate;
    private int vanePadHours;
    private Timestamp vanePadDate;
    private String status;
    private String remarks;
    private String boilerRemarks;

}
