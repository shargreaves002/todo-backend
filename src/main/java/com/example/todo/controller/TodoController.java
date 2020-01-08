package com.example.todo.controller;

import java.net.URI;
import java.util.Optional;
import com.example.todo.jwt.JwtTokenUtil;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins="http://localhost:3000")
@RestController
public class TodoController {

    private TodoService todoService;
    private JwtTokenUtil jwtTokenUtil;
    private UserService userService;

    @Autowired
    public TodoController(TodoService todoService, JwtTokenUtil jwtTokenUtil, UserService userService){
        this.todoService = todoService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }


    @GetMapping("/users/{username}/todos")
    public ResponseEntity<?> getAllTodosByUsername(@RequestHeader(name = "authorization") String token, @PathVariable String username){
        UserDetails user = userService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(token.substring(7), user))
            return new ResponseEntity<>(todoService.findByUsername(username), HttpStatus.OK);
        else
            return new ResponseEntity<>("You are not authorized.", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/users/{username}/todos/{id}")
    public ResponseEntity<?> getTodo(@RequestHeader(name = "authorization") String token, @PathVariable String username, @PathVariable long id){
        UserDetails user = userService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(token.substring(7), user)){
            Optional<Todo> todo = todoService.findById(id);
            if (todo.isPresent()){
                return new ResponseEntity<>(todo.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No todo with that ID found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("You are not authorized.", HttpStatus.FORBIDDEN);
        }
    }

    //DELETE /users/{username}/todos/{id}
    @DeleteMapping("/users/{username}/todos/{id}")
    public ResponseEntity<?> deleteTodo(@RequestHeader(name = "authorization") String token, @PathVariable String username, @PathVariable long id){
        UserDetails user = userService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(token.substring(7), user)){
            todoService.deleteById(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return new ResponseEntity<>("You are not authorized.", HttpStatus.FORBIDDEN);
        }
    }


    // Edit/Update
    //PUT /users/{user_name}/todos/{todo_id}
    @PutMapping("/users/{username}/todos/{id}")
    public ResponseEntity<?> updateTodo(@RequestHeader(name = "authorization") String token,
                                        @PathVariable String username,
                                        @PathVariable long id,
                                        @RequestBody Todo todo){
        UserDetails user = userService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(token.substring(7), user)) {
            if (todoService.update(id, todo) != null) {
                return new ResponseEntity<>(todoService.update(id, todo), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Todo not found.", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("You are not authorized.", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/users/{username}/todos")
    public ResponseEntity<Void> createTodo(
            @PathVariable String username, @RequestBody Todo todo){

        Todo createdTodo = todoService.create(todo, username);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdTodo.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
}
