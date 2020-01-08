package com.example.todo.repository;

import com.example.todo.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<User, Long> {
    User findByUsername(String username);
}
