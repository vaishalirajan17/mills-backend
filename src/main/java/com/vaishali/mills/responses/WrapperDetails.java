package com.vaishali.mills.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WrapperDetails {

    private List<ComponentDetails> cmptDetails;

    private List<RotorRunningDetails> rotorRunningDetails;

    public WrapperDetails(List<ComponentDetails> cmptDetails, List<RotorRunningDetails> rotorRunningDetails) {
        this.cmptDetails = cmptDetails;
        this.rotorRunningDetails = rotorRunningDetails;
    }
}
