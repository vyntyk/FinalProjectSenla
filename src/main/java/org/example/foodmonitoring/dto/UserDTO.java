package org.example.foodmonitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    // Getters и Setters
    @Schema(description = "ИМЯ", example = "Ivan")
    @NotBlank
    private String username;

    @Schema(description = "Пароль", example = "password")
    @NotBlank
    private String password;

    @Schema(description = "Почта", example = "ivan@example.com")
    @Email
    private String email;

    @Schema(description = "Роль", example = "USER")
    @NotBlank
    private String role;

}
