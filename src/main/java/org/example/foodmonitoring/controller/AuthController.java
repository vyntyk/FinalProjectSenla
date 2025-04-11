package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.UserDto;
import org.example.foodmonitoring.entity.Role;
import org.example.foodmonitoring.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        userService.registerUser(userDto.getUsername(), userDto.getPassword(), Role.USER);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable Long id, @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.ok("Профиль пользователя успешно обновлен");
    }
    @PostMapping("/login")
    public RedirectView login(@RequestBody UserDto userDto) {
        // Здесь можно добавить логику аутентификации
        // Например, проверка имени пользователя и пароля
        return new RedirectView("/success");
    }
}
