package com.example.todo.repository;


import java.util.List;

import com.example.todo.model.Todo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends CassandraRepository<Todo, Long> {
    List<Todo> findByUsername(String username);
}
