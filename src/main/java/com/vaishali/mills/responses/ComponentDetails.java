package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
public class ComponentDetails {

    private String millId;
    private String rotorId;
    private String componentName;
    private int runningHours;
    private Timestamp overAllDate;

}
