package com.task.api.dto;

import java.util.List;

public class UserResponseDTO {

    private String username;
    private List<?> tasks;
    private String message;

    public UserResponseDTO(String username, List<?> tasks, String message){
        this.username = username;
        this.tasks = tasks;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public List<?> getTasks() {
        return tasks;
    }

    public String getMessage() {
        return message;
    }
}
