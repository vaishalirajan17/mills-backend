package com.vaishali.mills.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter public class MillRotorMap {

    public String rotorId;

    public String millId;

    public String getRotorId() {
        return rotorId;
    }

    public void setRotorId(String rotorId) {
        this.rotorId = rotorId;
    }

    public String getMillId() {
        return millId;
    }

    public void setMillId(String millId) {
        this.millId = millId;
    }
}
