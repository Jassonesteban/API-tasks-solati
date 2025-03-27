package com.task.api.validation;

import com.task.api.dto.UserRequestDTO;
import com.task.api.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validate(UserRequestDTO userRequest){
        if(userRequest.getUsername() == null || userRequest.getUsername().trim().isEmpty()) {
            throw new CustomException("El nombre de usuario no puede estar vacio", HttpStatus.BAD_REQUEST);
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 6) {
            throw new CustomException("La contraseña debe tener al menos 6 caracteres", HttpStatus.BAD_REQUEST);
        }
    }
}
