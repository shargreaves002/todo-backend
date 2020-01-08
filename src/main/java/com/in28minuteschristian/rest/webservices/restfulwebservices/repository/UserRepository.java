package com.in28minuteschristian.rest.webservices.restfulwebservices.repository;

import com.in28minuteschristian.rest.webservices.restfulwebservices.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<User, Long> {
    User findByUsername(String username);
}
