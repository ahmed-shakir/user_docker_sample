package com.example.user.controllers;

import com.example.user.entities.Pet;
import com.example.user.services.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-API endpoints for user
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-15
 */
@RestController
@RequestMapping("/api/v1/pets")
@Slf4j
public class PetController {
    @Autowired
    private PetService petService;

    @Secured({"ROLE_ADMIN", "ROLE_EDITOR", "ROLE_USER"})
    @GetMapping
    public ResponseEntity<List<Pet>> findAllPets() {
        return ResponseEntity.ok(petService.findAll());
    }

    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @GetMapping("/{id}") // /api/v1/users/{id} -> localhost:7000/api/v1/users/{id}
    public ResponseEntity<Pet> findPetById(@PathVariable String id) {
        return ResponseEntity.ok(petService.findById(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Pet> savePet(@Validated @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.save(pet));
    }

    @Secured({"ROLE_ADMIN", "ROLE_EDITOR", "ROLE_USER"})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePet(@PathVariable String id, @Validated @RequestBody Pet pet) {
        petService.update(id, pet);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable String id) {
        petService.delete(id);
    }
}
