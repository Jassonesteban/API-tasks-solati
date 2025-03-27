package com.task.api.Mapper;

import com.task.api.dto.TaskDTO;
import com.task.api.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }

    public static List<TaskDTO> toDTOList(List<Task> tasks) {
        return tasks.stream().map(TaskMapper::toDTO).collect(Collectors.toList());
    }
}
