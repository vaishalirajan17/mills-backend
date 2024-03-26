package com.vaishali.mills.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class RemarkRequest {

    private String remarks;
    private String millId;
    private String remarkLoginId;
    private String remarkType;

    @Override
    public String toString() {
        return "RemarkRequest{" +
                "remarks='" + remarks + '\'' +
                ", millId='" + millId + '\'' +
                ", remarkLoginId='" + remarkLoginId + '\'' +
                ", remarkType='" + remarkType + '\'' +
                '}';
    }
}
