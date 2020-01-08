package com.in28minuteschristian.rest.webservices.restfulwebservices.controller;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.Session;
import com.in28minuteschristian.rest.webservices.restfulwebservices.model.Todo;
import com.in28minuteschristian.rest.webservices.restfulwebservices.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.datastax.driver.core.utils.UUIDs;

@CrossOrigin(origins="http://localhost:4200")
@RestController
public class TodoController {

    private TodoRepository todoRepository;

    CqlTemplate cqlTemplate;

    {
        Session session = Cluster.builder().addContactPoint("localhost").withoutJMXReporting().build().connect("todo");
        cqlTemplate = new CqlTemplate(session);
    }

    @Autowired
    public TodoController(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }


    @GetMapping("/users/{username}/todos")
    public ResponseEntity<?> getAllTodosByUsername(@PathVariable String username){
        return new ResponseEntity<>(todoRepository.findByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/users/{username}/todos/{id}")
    public ResponseEntity<?> getTodo(@PathVariable String username, @PathVariable long id){
        Optional<Todo> todo = todoRepository.findById(id);

        if (todo.isPresent()) return new ResponseEntity<>(todo.get(), HttpStatus.OK);
        else return new ResponseEntity<>("No model with that ID found", HttpStatus.NOT_FOUND);
    }

    //DELETE /users/{username}/todos/{id}
    @DeleteMapping("/users/{username}/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String username, @PathVariable long id){

        todoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
        //return ResponseEntity.notFound().build();
    }


    // Edit/Update
    //PUT /users/{user_name}/todos/{todo_id}
    @PutMapping("/users/{username}/todos/{id}")
    public ResponseEntity<?> updateTodo(
            @PathVariable String username,
            @PathVariable long id, @RequestBody Todo todo){
        AtomicReference<Todo> todo1 = new AtomicReference<>();
        todoRepository.findById(id).ifPresent(todo1::set);
        if (todo1.get() == null) return new ResponseEntity<>("Todo not found.", HttpStatus.NOT_FOUND);
        if (todo.getDescription() != null) todo1.get().setDescription(todo.getDescription());
        if (todo.getTargetDate() != null) todo1.get().setTargetDate(todo.getTargetDate());
        Todo todoUpdated = todoRepository.save(todo1.get());

        return new ResponseEntity<>(todoUpdated, HttpStatus.OK);
    }

    @PostMapping("/users/{username}/todos")
    public ResponseEntity<Void> createTodo(
            @PathVariable String username, @RequestBody Todo todo){

        todo.setUsername(username);
        todo.setId(UUIDs.timeBased());
        if (todo.getTargetDate() == null) {
            todo.setTargetDate(LocalDate.fromMillisSinceEpoch(Date.from(Instant.now().plusSeconds(86400)).getTime()));
        }
        Todo createdTodo = todoRepository.save(todo);

        //Location
        //Get current resource url
        ///{id}
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdTodo.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
}
