package com.user.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.application.UserService;
import com.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final User testUser = User.builder()
            .username("testuser")
            .name("Test User")
            .email("test@example.com")
            .gender("male")
            .picture("http://example.com/test.jpg")
            .country("Spain")
            .state("Madrid")
            .city("Madrid")
            .build();

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() throws Exception {
        List<User> users = Collections.singletonList(testUser);
        Page<User> page = new PageImpl<>(users);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].username").value("testuser"));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    void getUser_WhenExists_ShouldReturnUser() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUser_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(userService.getUserByUsername("nonexistent")).thenThrow(
                new RuntimeException("Usuario no encontrado")
        );

        mockMvc.perform(get("/api/users/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User updatedUser = testUser.toBuilder().name("Updated Name").build();

        when(userService.updateUser(eq("testuser"), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser("testuser");

        mockMvc.perform(delete("/api/users/testuser"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser("testuser");
    }

    @Test
    void generateUsers_ShouldReturnGeneratedUsers() throws Exception {
        List<User> generatedUsers = Collections.singletonList(testUser);

        when(userService.generateRandomUsers(5)).thenReturn(generatedUsers);

        mockMvc.perform(get("/api/users/generate/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getUsersTree_ShouldReturnTreeStructure() throws Exception {
        Map<String, Map<String, Map<String, List<User>>>> tree = new HashMap<>();
        tree.put("Spain", Map.of(
                "Madrid", Map.of("Madrid", Collections.singletonList(testUser))
        ));

        when(userService.getUsersTree()).thenReturn(tree);

        mockMvc.perform(get("/api/users/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Spain.Madrid.Madrid[0].username").value("testuser"));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        User invalidUser = User.builder().username("").email("invalid").build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }
}