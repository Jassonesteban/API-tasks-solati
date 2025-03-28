package com.task.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.api.controller.UserController;
import com.task.api.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserModel user;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");

        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.save(any(UserModel.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService, times(1)).save(any(UserModel.class));
    }

    @Test
    void testGetUserByUsername_Success() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetUserByUsername_NotFound() throws Exception {
        when(userService.findByUsername("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/unknownUser"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByUsername("unknownUser");
    }
}
