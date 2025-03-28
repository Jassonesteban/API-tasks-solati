package com.task.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.api.controller.TaskController;
import com.task.api.dto.TaskDTO;
import com.task.api.model.Status;
import com.task.api.model.Task;
import com.task.api.model.UserModel;
import com.task.api.validation.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private TaskValidator taskValidator;

    @InjectMocks
    private TaskController taskController;

    private UserModel user;
    private Task task;
    private TaskDTO taskDTO;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Status.PENDING);
        task.setUserModel(user);

        taskDTO = new TaskDTO(1L, "Updated task", "Updated Description", Status.COMPLETED);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateTask_Success() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskService.save(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks/testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskValidator, times(1)).validate(any(TaskDTO.class));
        verify(taskService, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTask_UserNotFound() throws Exception {
        when(userService.findByUsername("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/tasks/unknownUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isNotFound());

        verify(taskValidator, times(1)).validate(any(TaskDTO.class));
    }

    @Test
    void testGetTasksByUser_Success() throws Exception {
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(taskService.findAllByUser(user)).thenReturn(Arrays.asList(task));

        mockMvc.perform(get("/api/tasks/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(taskService, times(1)).findAllByUser(user);
    }

    @Test
    void testGetTasksByUser_UserNotFound() throws Exception {
        when(userService.findByUsername("unknownUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/unknownUser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTask() throws Exception {
        when(taskService.delete(1L)).thenReturn(task);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).delete(1L);
    }

    @Test
    void testUpdateTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(task);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskDTO.class));
    }
}
