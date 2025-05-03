package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.UserDTO;
import org.example.foodmonitoring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService);
    }

    @Test
    void testLoginPage_withError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("login"));
    }

    @Test
    void testLoginPage_withLogout() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logoutMessage"))
                .andExpect(view().name("login"));
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void testRegisterUser_success() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testUser")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    void testRegisterUser_failure() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(userService).register(Mockito.any(UserDTO.class));

        mockMvc.perform(post("/register")
                        .param("username", "testUser")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("register"));
    }

    @Test
    void testCreateUserPage() throws Exception {
        mockMvc.perform(get("/create-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));
    }

    @Test
    void testCreateUser_success() throws Exception {
        mockMvc.perform(post("/users/create")
                        .param("name", "testUser")
                        .param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/create-user?success"));
    }

    @Test
    void testCreateUser_failure() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(userService).register(Mockito.any(UserDTO.class));

        mockMvc.perform(post("/users/create")
                        .param("name", "testUser")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("create-user"));
    }

    @Test
    void testErrorPage() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}
