package com.example.todo.service;

import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.utils.UUIDs;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TodoService {

    private TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    public void updateUsername(String oldUsername, String newUsername) {
        List<Todo> todos = todoRepository.findAllByUsername(oldUsername);
        todos.forEach(v -> {
            v.setUsername(newUsername);
            todoRepository.save(v);
        });
    }

    public List<Todo> findByUsername(String username) {
        return todoRepository.findByUsername(username);
    }

    public Optional<Todo> findById(long id) {
        return todoRepository.findById(id);
    }

    public void deleteById(long id) {
        todoRepository.deleteById(id);
    }

    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo update(long id, Todo todo) {
        AtomicReference<Todo> todo1 = new AtomicReference<>();
        findById(id).ifPresent(todo1::set);
        if (todo1.get() == null) return null;
        if (todo.getDescription() != null) todo1.get().setDescription(todo.getDescription());
        if (todo.getTargetDate() != null) todo1.get().setTargetDate(todo.getTargetDate());
        return save(todo1.get());
    }

    public Todo create(Todo todo, String username) {
        todo.setUsername(username);
        todo.setId(UUIDs.timeBased());
        if (todo.getTargetDate() == null) {
            todo.setTargetDate(LocalDate.fromMillisSinceEpoch(Date.from(Instant.now().plusSeconds(86400)).getTime()));
        }
        return save(todo);
    }
}
