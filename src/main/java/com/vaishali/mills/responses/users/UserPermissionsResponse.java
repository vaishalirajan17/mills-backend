package com.vaishali.mills.responses.users;

import com.vaishali.mills.responses.GenericResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserPermissionsResponse extends GenericResponse {


    public UserPermissionsResponse(String errorMsg, String message, String errorCode) {
        super(errorMsg, message, errorCode);
    }

    List<Permission> permissions;
}