package com.vaishali.mills.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentResetRequest {

    private String rotorid;
    private String loginid;
    private String comptName;

    @Override
    public String toString() {
        return "ComponentResetRequest{" +
                "rotorid='" + rotorid + '\'' +
                ", loginid='" + loginid + '\'' +
                '}';
    }
}
