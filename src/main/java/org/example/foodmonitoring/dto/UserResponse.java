package org.example.foodmonitoring.dto;

import java.util.List;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
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
