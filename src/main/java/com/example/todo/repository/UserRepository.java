package com.example.todo.repository;

import com.example.todo.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findById(UUID id);
}
