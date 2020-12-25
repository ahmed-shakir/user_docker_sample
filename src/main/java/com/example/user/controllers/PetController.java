package com.example.user.controllers;

import com.example.user.entities.Pet;
import com.example.user.services.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "pet", description = "the pet API")
public class PetController {
    @Autowired
    private PetService petService;

    @Operation(summary = "Retrieve all pets", description = "Returns a list of pets.", tags = { "pet" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(type = "object")))
    })
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR", "ROLE_USER"})
    @GetMapping
    public ResponseEntity<List<Pet>> findAllPets() {
        return ResponseEntity.ok(petService.findAll());
    }

    @Operation(summary = "Find pet by ID", description = "For valid response try UUID as ID-value. Other values will generated exceptions.", tags = { "pet" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @GetMapping("/{id}") // /api/v1/users/{id} -> localhost:7000/api/v1/users/{id}
    public ResponseEntity<Pet> findPetById(@Parameter(description = "ID of pet to retrieve", required = true) @PathVariable String id) {
        return ResponseEntity.ok(petService.findById(id));
    }

    @Operation(summary = "New pet", description = "Create and save new pet. The pet object will be validated.", tags = { "pet" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation", content = @Content(schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "405", description = "Invalid input", content = @Content)
    })
    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Pet> savePet(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The new pet to save") @Validated @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.save(pet));
    }

    @Operation(summary = "Update pet", description = "Update an existing pet. The pet object will be validated.", tags = { "pet" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content(schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "405", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR", "ROLE_USER"})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePet(@Parameter(description = "ID of pet to update", required = true) @PathVariable String id, @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The updated pet to save") @Validated @RequestBody Pet pet) {
        petService.update(id, pet);
    }

    @Operation(summary = "Delete pet by ID", description = "For valid response try UUID as ID-value. Other values will generated exceptions.", tags = { "pet" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@Parameter(description = "ID of pet to delete", required = true) @PathVariable String id) {
        petService.delete(id);
    }
}
