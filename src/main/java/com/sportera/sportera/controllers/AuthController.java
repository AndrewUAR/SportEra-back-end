package com.sportera.sportera.controllers;

import com.sportera.sportera.models.User;
import com.sportera.sportera.services.UserService;
import com.sportera.sportera.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class  AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/api/1.0/signup")
    GenericResponse registerUser(@RequestBody User user) {
        userService.save(user);
        return new GenericResponse("User saved");
    }
}
