package com.example.user.services;

import com.example.user.entities.Pet;
import com.example.user.repositories.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * User service, business logic
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public Pet findById(String id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                        String.format("Could not find the pet by id %s.", id)));
    }

    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    public void update(String id, Pet pet) {
        if(!petRepository.existsById(id)) {
            log.error(String.format("Could not find the pet by id %s.", id));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                    String.format("Could not find the pet by id %s.", id));
        }
        pet.setId(id);
        petRepository.save(pet);
    }

    public void delete(String id) {
        if(!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                    String.format("Could not find the pet by id %s.", id));
        }
        petRepository.deleteById(id);
    }
}