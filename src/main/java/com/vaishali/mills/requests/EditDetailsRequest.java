package com.vaishali.mills.requests;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EditDetailsRequest {

    private String millId;
    private String rotorId;
    private String componentName;
    private String actionPerformed;
    private String loginId;
    private Timestamp editDate;

    @Override
    public String toString() {
        return "EditDetailsRequest{" +
                "millId='" + millId + '\'' +
                ", rotorId='" + rotorId + '\'' +
                ", componentName='" + componentName + '\'' +
                ", actionPerformed='" + actionPerformed + '\'' +
                ", loginId='" + loginId + '\'' +
                ", editDate=" + editDate +
                '}';
    }
}
