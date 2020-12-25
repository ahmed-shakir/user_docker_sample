package com.example.user.controllers;

import com.example.user.entities.User;
import com.example.user.repositories.UserRepository;
import com.example.user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <description>
 *
 * @author Ahmed Shakir
 * @version 1.0
 * @since 2020-11-01
 */
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(UUID.randomUUID().toString())
                .firstname("Kalle")
                .lastname("Karlsson")
                .birthday(LocalDate.of(1995, 5, 29))
                .mail("kalle@example.com")
                .phone("072-7278672")
                .username("kalle95")
                .password("password")
                .acl(List.of("ADMIN"))
                .build();
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    void findAllUsers() throws Exception {
        given(userService.findAll(null, false)).willReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    void findUserById() throws Exception {
        given(userService.findById(any())).willReturn(user);

        mockMvc.perform(get("/api/v1/users/{id}", user.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.mail", is(user.getMail())));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    void saveUser() throws Exception {
        String id = user.getId();
        user.setId(null);
        String userJson = objectMapper.writeValueAsString(user);
        userJson = userJson.replace("}", ",\"password\":\"" + user.getPassword() + "\"}");
        user.setId(id);

        given(userService.save(any())).willReturn(user);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.mail", is(user.getMail())));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    void updateUser() throws Exception {
        String userJson = objectMapper.writeValueAsString(user);
        userJson = userJson.replace("}", ",\"password\":\"" + user.getPassword() + "\"}");

        given(userRepository.existsById(any())).willReturn(true);

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    void deleteUser() throws Exception {
        given(userRepository.existsById(any())).willReturn(true);

        mockMvc.perform(delete("/api/v1/users/{id}", UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
