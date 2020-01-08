package com.in28minuteschristian.rest.webservices.restfulwebservices.controller;

import com.in28minuteschristian.rest.webservices.restfulwebservices.model.User;
import com.in28minuteschristian.rest.webservices.restfulwebservices.repository.UserRepository;
import com.in28minuteschristian.rest.webservices.restfulwebservices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="http://localhost:4200")
@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        User user1 = userService.save(user);
        if (user1 != null)
            return new ResponseEntity<>(user1, HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>("A user with this username already exists, please try again.", HttpStatus.CONFLICT);
    }
}
