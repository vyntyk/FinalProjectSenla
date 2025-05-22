package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.dto.UserDTO;
import org.example.foodmonitoring.service.UserService;
import org.example.foodmonitoring.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Авторизация",
        description = "Контроллер для регистрации и авторизации пользователей")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @Operation(
            operationId = "registerUser",
            summary = "Регистрация нового пользователя",
            description = "Создание нового пользователя. Новому пользователю автоматически назначается роль 'ROLE_USER'.",
            tags = {"Authentication"})
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Аутентификация пользователя и получение JWT токена",
            tags = {"Authentication"}
    )
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }
}
