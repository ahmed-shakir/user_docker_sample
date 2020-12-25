package com.example.user.controllers;

import com.example.user.entities.User;
import com.example.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST-API endpoints for user
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-10-15
 */
@RestController // REST API Endpoints
@RequestMapping("/api/v1/users")    // X.Y.Z
@Slf4j // logging
@Tag(name = "user", description = "the user API")
public class UserController {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private UserService userService;

    @Operation(summary = "Retrieve all users", description = "Returns a list of users. The list can be filtered by name and sorted by birthdate.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = User.class)))
    })
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR", "ROLE_USER"})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> findAllUsers(@Parameter(description = "The name to filter the user list on", required = false) @RequestParam(required = false) String name, @Parameter(description = "Flag to enable or disable sorting. If enabled the list is sorted on birthdate.", required = false) @RequestParam(required = false) boolean sort) {
        // var users = userService.findAll(); // List<User> users = userService.findAll();
        // this.users = users;
        // return new ResponseEntity<>(users, HttpStatus.OK);
        // return ResponseEntity.ok(users);
        return ResponseEntity.ok(userService.findAll(name, sort));
    }

    @Operation(summary = "Find user by ID", description = "For valid response try UUID as ID-value. Other values will generated exceptions.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findUserById(@Parameter(description = "ID of user to retrieve", required = true) @PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "New user", description = "Create and save new user. The user object will be validated.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "405", description = "Invalid input", content = @Content)
    })
    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The new user to save") @Validated @RequestBody User user) {
        var savedUser = userService.save(user);
        var uri = URI.create("/api/v1/users/" + savedUser.getId()); // /api/v1/users/1
        return ResponseEntity.created(uri).body(savedUser);
    }

    @Operation(summary = "Update user", description = "Update an existing user. The user object will be validated.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "405", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR", "ROLE_USER"})
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@Parameter(description = "ID of user to update", required = true) @PathVariable String id, @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The updated user to save") @Validated @RequestBody User user) {
        userService.update(id, user);
    }

    @Operation(summary = "Delete user by ID", description = "For valid response try UUID as ID-value. Other values will generated exceptions.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Parameter(description = "ID of user to delete", required = true) @PathVariable String id) {
        userService.delete(id);
    }

    @Operation(summary = "Initialize the user db", description = "If and only if the user db is empty, a new user with admin role is generated.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation")
    })
    @GetMapping("/init")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void init() {
        userService.init();
    }

    @Operation(summary = "OAuth user details", description = "Retrieves the user details from OAuth client.", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping(value = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> retrieveUserDetails(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        String userInfoEndpointUri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
            HttpEntity entity = new HttpEntity("", headers);
            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            return ResponseEntity.ok("Client name: " + userAttributes.get("name"));
        }
        return ResponseEntity.badRequest().build();
    }
}
