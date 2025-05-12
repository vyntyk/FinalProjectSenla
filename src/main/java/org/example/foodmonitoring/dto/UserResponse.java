package org.example.foodmonitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class UserResponse {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;
    
    @Schema(description = "ИМЯ", example = "Ivan")
    private String username;

    @Schema(description = "ПОЧТА", example = "ivan@gmail.com")
    private String email;

    @Schema(description = "Список ролей", example = "USER")
    private List<String> roles;

    public UserResponse(Long id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
