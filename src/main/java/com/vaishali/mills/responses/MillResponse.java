package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MillResponse extends GenericResponse{

    public MillResponse(String errorMsg, String message, String errorCode) {
        super(errorMsg, message, errorCode);
    }

    public List<String> mills;
}
