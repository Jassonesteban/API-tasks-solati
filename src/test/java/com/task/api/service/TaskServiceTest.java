package com.task.api.service;

import com.task.api.dto.TaskDTO;
import com.task.api.exception.CustomException;
import com.task.api.model.Status;
import com.task.api.model.Task;
import com.task.api.model.UserModel;
import com.task.api.repository.TaskRepository;
import com.task.api.validation.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskValidator taskValidator;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private UserModel user;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(1L);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.PENDING);
        task.setUserModel(user);

        taskDTO = new TaskDTO(1L, "Task Test", "Updated Description", Status.COMPLETED);
    }

    @Test
    void testFindAllByUser() {
        when(taskRepository.findByUserModel(user)).thenReturn(List.of(task));

        List<Task> tasks = taskService.findAllByUser(user);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());

        verify(taskRepository, times(1)).findByUserModel(user);
    }

    @Test
    void testFindById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.findById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getTitle());

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        when(taskRepository.save(task)).thenReturn(task);

        Task savedTask = taskService.save(task);

        assertNotNull(savedTask);
        assertEquals("Test Task", savedTask.getTitle());

        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        Task deletedTask = taskService.delete(1L);

        assertNotNull(deletedTask);
        assertEquals(1L, deletedTask.getId());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testDeleteThrowsExceptionIfTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> taskService.delete(1L));

        assertEquals("Tarea no encontrada", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void testUpdateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskValidator).validate(taskDTO);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTask(1L, taskDTO);

        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals("COMPLETED", updatedTask.getStatus());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskValidator, times(1)).validate(taskDTO);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskThrowsExceptionIfTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> taskService.updateTask(1L, taskDTO));

        assertEquals("Tarea no encontrada", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskValidator, never()).validate(any());
        verify(taskRepository, never()).save(any());
    }

}
