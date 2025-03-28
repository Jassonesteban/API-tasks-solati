package com.task.api.service;

import com.task.api.model.UserModel;
import com.task.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<UserModel> foundUser = userService.findByUsername("testUser");

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        Optional<UserModel> foundUser = userService.findByUsername("unknownUser");

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findByUsername("unknownUser");
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(user)).thenReturn(user);

        UserModel savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }
}
