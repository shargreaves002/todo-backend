package com.example.todo.controller;

import com.example.todo.exception.UsernameTakenException;
import com.example.todo.jwt.JwtTokenUtil;
import com.example.todo.model.Response;
import com.example.todo.model.User;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins="http://localhost:3000")
@RestController
public class UserController {

    private UserService userService;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController (UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            User user1 = userService.save(user);
            return new ResponseEntity<>(user1, HttpStatus.ACCEPTED);
        } catch (UsernameTakenException e) {
            return new ResponseEntity<>("A user with this username already exists, please choose a different username.", HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestHeader(name = "authorization") String token, @RequestBody User user) {
        Optional<User> user1 = userService.findById(user.getId());
        if (user1.isPresent()) {
            if (jwtTokenUtil.validateToken(token.substring(7), user1.get())) {
                if (user.getUsername() != null && userService.exist(user.getUsername())) {
                    return new ResponseEntity<>("A user with this username already exists, please choose a different username. No updates were saved.", HttpStatus.CONFLICT);
                } else {
                    Response response = userService.updateUser(user);
                    return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
                }
            } else {
                return new ResponseEntity<>("You are not authorized.", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("No user with that ID was found.", HttpStatus.NOT_FOUND);
        }
    }
}
