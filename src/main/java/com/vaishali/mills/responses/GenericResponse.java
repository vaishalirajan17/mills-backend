package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponse {

    private String errorMsg;
    private String message;
    private String errorCode;

    public GenericResponse(String errorMsg, String message, String errorCode) {
        this.errorMsg = errorMsg;
        this.message = message;
        this.errorCode = errorCode;
    }
}
