package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.UserDTO;
import org.example.foodmonitoring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final UserService userService;

    @Autowired
    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы успешно вышли из системы");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(UserDTO userDTO, Model model) {
        try {
            userService.register(userDTO);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при регистрации: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/create-user")
    public String createUserPage() {
        return "create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String name, 
                            @RequestParam String email, 
                            Model model) {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(name);
            userDTO.setEmail(email);
            // Generate a random password or use a default one
            userDTO.setPassword("defaultPassword");
            userService.register(userDTO);
            return "redirect:/create-user?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
            return "create-user";
        }
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}
