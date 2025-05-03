package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.UserRequest;
import org.example.foodmonitoring.dto.UserResponse;
import org.example.foodmonitoring.exception.UserNotFoundException;
import org.example.foodmonitoring.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;

@WebMvcTest(UserController.class)
@Import(org.example.foodmonitoring.config.TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenCreateUser_thenReturnCreatedUser() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(1L, "testuser", "test@example.com", List.of("USER"));

        Mockito.when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        String json = """
        {
          "username": "testuser",
          "password": "password",
          "email": "test@example.com",
          "role": "USER"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.roles[0]", is("USER")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenGetAllUsers_thenReturnUsersList() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(1L, "testuser", "test@example.com", List.of("USER"));

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")))
                .andExpect(jsonPath("$[0].email", is("test@example.com")))
                .andExpect(jsonPath("$[0].roles[0]", is("USER")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenDeleteUser_thenReturnSuccessMessage() throws Exception {
        // Arrange
        Mockito.when(userService.deleteUser(anyLong())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenDeleteNonExistentUser_thenReturnNotFound() throws Exception {
        // Arrange
        Mockito.when(userService.deleteUser(anyLong())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/users/999")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(1L, "updateduser", "updated@example.com", List.of("ADMIN"));

        Mockito.when(userService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(response);

        String json = """
        {
          "username": "updateduser",
          "password": "newpassword",
          "email": "updated@example.com",
          "role": "ADMIN"
        }
        """;

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.roles[0]", is("ADMIN")));
    }
}
