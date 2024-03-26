package com.vaishali.mills.controllers;

import com.vaishali.mills.constants.QueryConstants;
import com.vaishali.mills.requests.NewUserRequest;
import com.vaishali.mills.responses.GenericResponse;
import com.vaishali.mills.utils.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    QueryUtils queryUtils;
    private static final Logger logger = LoggerFactory.getLogger(MillController.class);
    @PostMapping("/user")
    public GenericResponse createUser(@RequestBody NewUserRequest newUserRequest) {
        logger.info(newUserRequest.toString());

        String query = String.format(QueryConstants.GET_USERNAME,
                newUserRequest.getUserName());

        int userCount = queryUtils.queryObjectStringWrapper(query).getState();

        if(userCount == 1 ) {
            return new GenericResponse("User name already exists.","","401.05");
        } else if(userCount == -1 ) {
            return new GenericResponse("Error in creating user. Please try again.","","401.06");
        }

        int result = queryUtils.insert(
                QueryConstants.INSERT_NEW_USER,
                newUserRequest.getUserName(),
                newUserRequest.getPassword(),
                newUserRequest.getRoleId(),
                "Y");

        if(result == -1) {
            return new GenericResponse("Error in creating user. Please try again.","","401.06");
        }

        return new GenericResponse("","Success","");
    }

}
