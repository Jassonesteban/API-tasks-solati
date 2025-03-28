package com.task.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.api.controller.AuthController;
import com.task.api.dto.AuthResponseDTO;
import com.task.api.dto.UserRequestDTO;
import com.task.api.dto.UserResponseDTO;
import com.task.api.exception.CustomException;
import com.task.api.model.UserModel;
import com.task.api.security.JwtUtil;
import com.task.api.security.LoginAttemptService;
import com.task.api.service.UserService;
import com.task.api.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private UserModel user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        objectMapper = new ObjectMapper();

        user = new UserModel();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("testUser");
        userRequestDTO.setPassword("password123");
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userService.save(any(UserModel.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.message").value("Bienvenido a la API"));

        verify(userService, times(1)).save(any(UserModel.class));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Exception exception = result.getResolvedException();

        assertNotNull(exception);
        assertInstanceOf(CustomException.class, exception);
        assertEquals("El usuario ya existe", exception.getMessage());

        verify(userService, never()).save(any(UserModel.class));
    }


    @Test
    void testLogin_Success() throws Exception {
        when(loginAttemptService.isBlocked("testUser")).thenReturn(false);
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("testUser")).thenReturn("mockedJwtToken");

        UserModel loginRequest = new UserModel();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.token").value("mockedJwtToken"));

        verify(loginAttemptService, times(1)).loginSucceeded("testUser");
    }

    @Test
    void testLogin_Failed_InvalidCredentials() throws Exception {
        when(loginAttemptService.isBlocked("testUser")).thenReturn(false);
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(false);

        UserModel loginRequest = new UserModel();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        Exception exception = result.getResolvedException();

        assertNotNull(exception);
        assertInstanceOf(CustomException.class, exception);
        assertEquals("Credenciales incorrectas", exception.getMessage());

        verify(loginAttemptService, times(1)).loginFailed("testUser");
    }

    @Test
    void testLogin_Failed_UserBlocked() throws Exception {
        when(loginAttemptService.isBlocked("testUser")).thenReturn(true);

        UserModel loginRequest = new UserModel();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests())
                .andReturn();

        Exception exception = result.getResolvedException();

        assertNotNull(exception);
        assertInstanceOf(CustomException.class, exception);
        assertTrue(exception.getMessage().contains("Usuario bloqueado temporalmente"));

        verify(userService, never()).findByUsername(anyString());
    }
}
