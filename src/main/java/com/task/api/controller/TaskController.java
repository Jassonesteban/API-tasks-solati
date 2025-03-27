package com.task.api.controller;

import com.task.api.Mapper.TaskMapper;
import com.task.api.dto.TaskDTO;
import com.task.api.model.Task;
import com.task.api.model.UserModel;
import com.task.api.service.TaskService;
import com.task.api.service.UserService;
import com.task.api.validation.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskValidator taskValidator;

    @PostMapping("/{username}")
    public ResponseEntity<?> createTask(@PathVariable String username, @RequestBody TaskDTO taskDTO){
        taskValidator.validate(taskDTO);

        Optional<UserModel> user = userService.findByUsername(username);
        if(user.isPresent()){
            Task task = new Task();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setUserModel(user.get());

            Task savedTask = taskService.save(task);
            return ResponseEntity.ok(savedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable String username){
        Optional<UserModel> user = userService.findByUsername(username);
        return user.map(value -> ResponseEntity.ok(taskService.findAllByUser(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long id){
        Task deletedTask = taskService.delete(id);
        return ResponseEntity.ok(deletedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO){
        Task updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }
}
