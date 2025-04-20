package org.example.foodmonitoring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

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

    // Getters and Setters
}
