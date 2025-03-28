package com.task.api.controller;

import com.task.api.dto.AuthResponseDTO;
import com.task.api.dto.UserRequestDTO;
import com.task.api.dto.UserResponseDTO;
import com.task.api.exception.CustomException;
import com.task.api.model.UserModel;
import com.task.api.security.JwtUtil;
import com.task.api.security.LoginAttemptService;
import com.task.api.service.UserService;
import com.task.api.validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserValidator userValidator;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                          UserDetailsService userDetailsService, UserService userService, UserValidator userValidator, LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.userValidator = userValidator;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserModel loginRequest) {
        String username = loginRequest.getUsername();

        //validar si el usuario existe
        if(loginAttemptService.isBlocked(username)){
            throw new CustomException("Usuario bloqueado temporalmente. intente dentro de 5 minutos.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Optional<UserModel> user = userService.findByUsername(username);

        if(user.isEmpty()){
            loginAttemptService.loginFailed(username);
            throw new CustomException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }

        UserModel userModel = user.get();

        if(!passwordEncoder.matches(loginRequest.getPassword(), userModel.getPassword())){
            loginAttemptService.loginFailed(username);
            throw new CustomException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }

        loginAttemptService.loginSucceeded(username);

        String token = jwtUtil.generateToken(username);
        AuthResponseDTO response = new AuthResponseDTO(token, username, "Inicio de sesión exitoso");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        userValidator.validate(userRequestDTO);

        if (userService.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            throw new CustomException("El usuario ya existe", HttpStatus.BAD_REQUEST);
        }

        UserModel user = new UserModel();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setTasks(new ArrayList<>());

        UserModel savedUser = userService.save(user);

        UserResponseDTO response = new UserResponseDTO(
                savedUser.getUsername(),
                savedUser.getTasks(),
                "Bienvenido a la API"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
