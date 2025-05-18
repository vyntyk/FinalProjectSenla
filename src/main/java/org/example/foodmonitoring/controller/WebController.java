package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.dto.UserDTO;
import org.example.foodmonitoring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.Random;

@Controller
@Tag(name = "Web Pages", description = "Endpoints for rendering web pages")
public class WebController {

    private final UserService userService;

    @Autowired
    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    @Operation(
            summary = "Display login page",
            description = "Renders the login page for user authentication",
            tags = {"Web Pages"}
    )
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы успешно вышли из системы");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Регистрация прошла успешно. Пожалуйста, войдите.");
        }
        return "login";
    }

    @GetMapping("/register")
    @Operation(
            summary = "Показать страницу регистрации",
            description = "Renders the registration page for new users",
            tags = {"Web Pages"}
    )
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    @Operation(
            summary = "Process user registration",
            description = "Handles form submission for registering a new user",
            tags = {"Web Pages"}
    )
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setEmail(email);
            userDTO.setPassword(password);
            userService.register(userDTO);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Ошибка: " + e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при регистрации. Пожалуйста, попробуйте снова.");
            return "register";
        }
    }

    @GetMapping("/create-user")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Display create user page",
            description = "Renders the page for creating a new user (requires authentication)",
            tags = {"Web Pages"}
    )
    public String createUserPage() {
        return "create-user";
    }

    @PostMapping("/users/create")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Process user creation",
            description = "Handles form submission for creating a new user (requires authentication)",
            tags = {"Web Pages"}
    )
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             Model model) {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setEmail(email);
            userDTO.setPassword(password.isEmpty() ? generateRandomPassword() : password);
            userService.register(userDTO);
            return "redirect:/create-user?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
            return "create-user";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании пользователя. Пожалуйста, попробуйте снова.");
            return "create-user";
        }
    }

    @GetMapping("/error")
    @Operation(
            summary = "Display error page",
            description = "Renders the error page for handling application errors",
            tags = {"Web Pages"}
    )
    public String errorPage() {
        return "error";
    }

    private String generateRandomPassword() {
        // Generate a secure random password (e.g., 12 characters)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}