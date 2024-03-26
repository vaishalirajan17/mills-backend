package com.vaishali.mills.service;

import com.vaishali.mills.constants.QueryConstants;
import com.vaishali.mills.requests.login.LoginRequest;
import com.vaishali.mills.responses.GenericResponse;
import com.vaishali.mills.responses.QueryWrapper;
import com.vaishali.mills.responses.mappers.PermissionsMapper;
import com.vaishali.mills.responses.users.Permission;
import com.vaishali.mills.responses.users.UserPermissionsResponse;
import com.vaishali.mills.utils.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    QueryUtils queryUtils;

    public GenericResponse handleLoginRequest(LoginRequest loginRequest) {
        if(isInvalidLoginRequest(loginRequest)) {
            return new GenericResponse("User name/ Password cannot be empty", "", "");
        }

        // check if username and password is valid
        String query = String.format(QueryConstants.GET_ACTIVE_USER_BY_USERNAME_AND_PASSWORD,
                loginRequest.getUsername(),
                loginRequest.getPassword());


        QueryWrapper result = queryUtils.queryObjectStringWrapper(query);

        if(result.getState() == -1) {
            // exception occured
            return new GenericResponse("Error in login. Please try again later", "", "401.06");
        } else if (result.getState() == 0) {
            return new GenericResponse("Wrong username/password. Please check again", "", "401.07");
        } else {
            logger.info("successfully logged in for user {}", loginRequest.getUsername());
            return new GenericResponse("","Logged in", "");
        }
    }

    public GenericResponse handlePasswordChangeRequest(LoginRequest loginRequest) {
        if(isInvalidLoginRequest(loginRequest)) {
            return new GenericResponse("User name/ Password cannot be empty", "", "");
        }

        String query = String.format(QueryConstants.UPDATE_PASSWORD_BY_USERNAME,
                loginRequest.getPassword(),
                loginRequest.getUsername());

        int result = queryUtils.update(query);

        if(result == -1) {
            // exception occured
            return new GenericResponse("Error in changing password. Please try again later", "", "401.08");
        } else {
            logger.info("successfully changed password for user {}", loginRequest.getUsername());
            return new GenericResponse("","Changed password successfully", "");
        }

    }


    public UserPermissionsResponse handlePermissionsRequest(String username) {

        if(isInvalidPermissionsRequest(username)) {
            return new UserPermissionsResponse("Username is required", "", "");
        }

        List<Permission> permissionList = getPermissions(username);

        if(permissionList == null) {
            return new UserPermissionsResponse("Error in getting permissions for the user", "", "");
        }
        UserPermissionsResponse response = new UserPermissionsResponse("", "Success", "");
        response.setPermissions(permissionList);
        return response;
    }

    public List<Permission> getPermissions(String roleId) {

        String query = String.format(
                QueryConstants.GET_PERMISSIONS_BY_USERNAME,
                roleId
        );

        return queryUtils.getDataFromDB(query, new PermissionsMapper());
    }


    public boolean isInvalidLoginRequest(LoginRequest loginRequest) {
        return loginRequest.getUsername() == null || loginRequest.getPassword() == null;
    }

    public boolean isInvalidPermissionsRequest(String roleId) {
        return roleId == null;
    }

}
