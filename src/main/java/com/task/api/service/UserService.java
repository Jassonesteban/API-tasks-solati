package com.task.api.service;

import com.task.api.model.UserModel;
import com.task.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserModel save(UserModel userModel){
        return userRepository.save(userModel);
    }

}
