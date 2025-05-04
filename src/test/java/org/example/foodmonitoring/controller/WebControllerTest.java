package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.UserDTO;
import org.example.foodmonitoring.security.JwtTokenProvider;
import org.example.foodmonitoring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, jwtTokenProvider);
    }

    @Test
    @WithMockUser
    void register_withCsrf_shouldSucceed() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testUser")
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }


    @Test
    @WithMockUser(roles = "ANONYMOUS")
    void testRegisterUser_failure() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(userService).register(Mockito.any(UserDTO.class));

        mockMvc.perform(post("/register")
                        .param("username", "testUser")
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .with(csrf())) // добавляем CSRF токен
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("register"));
    }
}
