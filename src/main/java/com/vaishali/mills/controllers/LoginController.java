package com.vaishali.mills.controllers;


import com.vaishali.mills.requests.login.LoginRequest;
import com.vaishali.mills.responses.GenericResponse;
import com.vaishali.mills.responses.users.UserPermissionsResponse;
import com.vaishali.mills.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class LoginController {

    @Autowired
    LoginService loginService;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/login")
    public GenericResponse login(@RequestBody LoginRequest loginRequest) {
        logger.info(String.valueOf(loginRequest));

        return loginService.handleLoginRequest(loginRequest);
    }

    @GetMapping("/permissions")
    public UserPermissionsResponse getPermissionsByUsername(@RequestParam(name = "username") String username) {
        logger.info("/permissions for username : {}", username);

        return loginService.handlePermissionsRequest(username);
    }


}
