package com.task.api.service;

import com.task.api.dto.TaskDTO;
import com.task.api.exception.CustomException;
import com.task.api.model.Task;
import com.task.api.model.UserModel;
import com.task.api.respoitory.TaskRepository;
import com.task.api.validation.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskValidator taskValidator;

    public List<Task> findAllByUser(UserModel userModel){
        return taskRepository.findByUserModel(userModel);
    }

    public Optional<Task> findById(long id){
        return taskRepository.findById(id);
    }

    public Task save(Task task){
        return taskRepository.save(task);
    }

    public Task delete(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Tarea no encontrada", HttpStatus.NOT_FOUND));
        taskRepository.delete(task);

        return task;
    }

    public Task updateTask(Long id, TaskDTO taskDTO){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException("Tarea no encontrada", HttpStatus.NOT_FOUND));

        taskValidator.validate(taskDTO);

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());

        return taskRepository.save(task);
    }
}
