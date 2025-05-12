package org.example.foodmonitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequest {
    @Schema(description = "ИМЯ", example = "Ivan")
    @NotBlank
    private String username;

    @Schema(description = "ПАРОЛЬ", example = "123456")
    @NotBlank
    private String password;

    @Schema(description = "ПОЧТА", example = "ivan@mail.ru")
    @Email
    private String email;

    @Schema(description = "РОЛЬ", example = "USER")
    @NotBlank
    private String role;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public CharSequence getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
