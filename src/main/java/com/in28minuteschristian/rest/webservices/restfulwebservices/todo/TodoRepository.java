package com.in28minuteschristian.rest.webservices.restfulwebservices.todo;


import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends CassandraRepository<Todo, Long> {
    List<Todo> findByUsername(String username);
}
