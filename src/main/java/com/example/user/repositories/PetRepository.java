package com.example.user.repositories;

import com.example.user.entities.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-26
 */
public interface PetRepository extends MongoRepository<Pet, String> {
}
