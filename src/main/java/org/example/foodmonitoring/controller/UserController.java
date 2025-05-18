package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.dto.UserRequest;
import org.example.foodmonitoring.dto.UserResponse;
import org.example.foodmonitoring.exception.UserNotFoundException;
import org.example.foodmonitoring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Контроллер пользователя", description = "Работа с пользователями")
@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @SecurityRequirement(name = "JWT")
    // Создание пользователя
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "JWT")
    // Получение всех пользователей
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @SecurityRequirement(name = "JWT")
    // Удаление пользователя по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @SecurityRequirement(name = "JWT")
    // Редактирование пользователя по ID
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userRequest) throws UserNotFoundException {
        UserResponse response = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(response);
    }
}