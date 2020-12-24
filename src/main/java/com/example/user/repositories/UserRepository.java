package com.example.user.repositories;

import com.example.user.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for user entity
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-15
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
