package com.vaishali.mills.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class NewUserRequest {
    private String userName;
    private String password;
    private String roleId;
}
