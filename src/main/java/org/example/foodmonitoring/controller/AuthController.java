package org.example.foodmonitoring.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.example.foodmonitoring.service.UserService;
import org.example.foodmonitoring.entity.Role;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
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
            userService.registerUser(username, password, Role.USER);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
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
            userService.authenticateUser(username, password);
            return "redirect:/home"; // Перенаправление на главную страницу после успешного входа
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login"; // Перенаправление на страницу логина при заходе на localhost
    }
}
