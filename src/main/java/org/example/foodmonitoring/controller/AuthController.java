package org.example.foodmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodmonitoring.entity.Role;
import org.example.foodmonitoring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login() {
        return "login"; // Возвращает имя шаблона страницы логина
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model
    ) {
        try {
            validateInput(username, password);
            userService.createUser(username, password);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка регистрации пользователя: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/auth/login")
    public String loginUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model
    ) {
        try {
            validateInput(username, password);
            userService.authenticateUser(username, password);
            return "redirect:/home"; // Перенаправление на главную страницу после успешного входа
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка аутентификации пользователя: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login"; // Перенаправление на страницу логина при заходе на localhost
    }

    private void validateInput(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать не менее 6 символов.");
        }
    }
}
