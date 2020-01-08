package com.in28minuteschristian.rest.webservices.restfulwebservices.repository;


import java.util.List;

import com.in28minuteschristian.rest.webservices.restfulwebservices.model.Todo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends CassandraRepository<Todo, Long> {
    List<Todo> findByUsername(String username);
}
