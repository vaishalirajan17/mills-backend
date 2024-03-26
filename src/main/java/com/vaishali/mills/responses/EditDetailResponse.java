package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EditDetailResponse {

    private String  millId;
    private String rotorId;
        private Timestamp editDate;
    private String actionPerformed;

}
