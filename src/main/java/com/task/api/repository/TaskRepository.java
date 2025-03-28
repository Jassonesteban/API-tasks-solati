package com.task.api.repository;

import com.task.api.model.Task;
import com.task.api.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserModel(UserModel userModel);
}
