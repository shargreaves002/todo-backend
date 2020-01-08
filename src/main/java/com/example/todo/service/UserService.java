package com.example.todo.service;

import com.datastax.driver.core.utils.UUIDs;
import com.example.todo.exception.UsernameTakenException;
import com.example.todo.jwt.JwtTokenUtil;
import com.example.todo.jwt.resource.JwtTokenResponse;
import com.example.todo.model.Response;
import com.example.todo.model.User;
import com.example.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private JwtTokenUtil jwtTokenUtil;

    private TodoService todoService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, TodoService todoService){
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.todoService = todoService;
    }

    public User save(User user) throws UsernameTakenException {
        if (userRepository.findByUsername(user.getUsername()) != null)
            throw new UsernameTakenException("Username taken.");

        user.setId(UUIDs.timeBased());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s);
    }

    public Response updateUser(User user) {
        Optional<User> user1 = userRepository.findById(user.getId());
        Response response = new Response();
        user1.ifPresent(v -> {
            if (user.getUsername() == null || userRepository.findByUsername(user.getUsername()) == null) {
                if (user.getUsername() != null) {
                    todoService.updateUsername(v.getUsername(), user.getUsername());
                    v.setUsername(user.getUsername());
                }
                if (user.getPassword() != null) v.setPassword(user.getPassword());
                if (user.getRole() != null) v.setRole(user.getRole());
                userRepository.save(v);
                response.setData(v, new JwtTokenResponse(jwtTokenUtil.generateToken(v)));
            }
        });
        return response;
    }

    public boolean exist(String newUsername) {
        return userRepository.findByUsername(newUsername) != null;
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
}
