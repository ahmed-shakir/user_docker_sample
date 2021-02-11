package com.example.user.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.user.entities.Pet;
import com.example.user.repositories.PetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private static final String ERROR_MSG_WITH_PARAM = "Could not find the pet by id %s.";

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public Pet findById(String id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                        String.format(ERROR_MSG_WITH_PARAM, id)));
    }

    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    public void update(String id, Pet pet) {
        if(!petRepository.existsById(id)) {
            String errorMsg = String.format(ERROR_MSG_WITH_PARAM, id);
            log.error(errorMsg);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg); // 404 -> Not found
        }
        pet.setId(id);
        petRepository.save(pet);
    }

    public void delete(String id) {
        if(!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, // 404 -> Not found
                    String.format(ERROR_MSG_WITH_PARAM, id));
        }
        petRepository.deleteById(id);
    }
}