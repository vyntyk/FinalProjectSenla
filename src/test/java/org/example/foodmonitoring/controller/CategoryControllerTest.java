package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.entity.Category;
import org.example.foodmonitoring.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
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

@WebMvcTest(controllers = CategoryController.class)
@Import(org.example.foodmonitoring.config.TestSecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenGetAllCategories_thenReturnCategoriesList() throws Exception {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(category));

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Category")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAddCategory_thenReturnSuccessMessage() throws Exception {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Mockito.when(categoryService.addCategory(anyString())).thenReturn(category);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"Test Category\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Категория добавлена"));
    }
}
