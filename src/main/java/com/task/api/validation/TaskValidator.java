package com.task.api.validation;

import com.task.api.dto.TaskDTO;
import com.task.api.dto.UserRequestDTO;
import com.task.api.exception.CustomException;
import com.task.api.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TaskValidator {

    public void validate(TaskDTO taskDTO){
        if(taskDTO.getTitle()== null || taskDTO.getTitle().trim().isEmpty()) {
            throw new CustomException("Debe ingresar un titulo valido a la nota", HttpStatus.BAD_REQUEST);
        }

        if(taskDTO.getDescription() == null || taskDTO.getDescription().trim().isEmpty()) {
            throw new CustomException("Ingrese una breve descripcion de la tarea", HttpStatus.BAD_REQUEST);
        }

        if (taskDTO.getStatus() == Status.COMPLETED && (taskDTO.getTitle().isEmpty() || taskDTO.getDescription().isEmpty())) {
            throw new CustomException("No puede marcar la tarea como COMPLETED sin título o descripción", HttpStatus.BAD_REQUEST);
        }

        if (!Arrays.asList(Status.values()).contains(taskDTO.getStatus())) {
            throw new CustomException("Estado inválido. Los valores permitidos son: PENDING, COMPLETED", HttpStatus.BAD_REQUEST);
        }

    }
}
