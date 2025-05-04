package org.example.foodmonitoring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    // Getters и Setters
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    @NotBlank
    private String role;

}
