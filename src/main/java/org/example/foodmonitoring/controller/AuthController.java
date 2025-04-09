package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.service.UserService;

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
}