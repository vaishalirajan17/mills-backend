package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentDetails {

    private String millId;
    private String rotorId;
    private String componentName;
    private int runningHours;
    private boolean passed;

}
