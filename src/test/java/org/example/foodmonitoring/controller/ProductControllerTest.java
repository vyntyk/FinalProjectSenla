package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

@WebMvcTest(controllers = ProductController.class)
@Import(org.example.foodmonitoring.config.TestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenCreateProduct_thenReturnCreatedProduct() throws Exception {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setCategoryId(1L); // Changed from setCategory("Food")
        productDTO.setDescription("Test Description");

        Mockito.when(productService.create(any(ProductDTO.class))).thenReturn(productDTO);

        // JSON payload now uses categoryId (Long)
        String json = """
        {
          "name": "Test Product",
          "categoryId": 1, 
          "description": "Test Description"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.categoryId", is(1))) // Changed from category to categoryId
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenListProducts_thenReturnProductsList() throws Exception {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setCategoryId(1L); // Changed from setCategory("Food")
        productDTO.setDescription("Test Description");

        Mockito.when(productService.list(anyString(), anyLong())).thenReturn(List.of(productDTO)); // anyString() to anyLong()

        // Act & Assert
        mockMvc.perform(get("/products")
                .param("name", "Test")
                .param("categoryId", "1")) // Changed param name and value
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].categoryId", is(1))) // Changed from category to categoryId
                .andExpect(jsonPath("$[0].description", is("Test Description")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
        // Arrange
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Updated Product");
        productDTO.setCategoryId(1L); // Changed from setCategory("Updated Food")
        productDTO.setDescription("Updated Description");

        Mockito.when(productService.update(any(ProductDTO.class))).thenReturn(productDTO);

        // JSON payload now uses categoryId (Long)
        String json = """
        {
          "name": "Updated Product",
          "categoryId": 1,
          "description": "Updated Description"
        }
        """;

        // Act & Assert
        mockMvc.perform(put("/products/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.categoryId", is(1))) // Changed from category to categoryId
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenDeleteProduct_thenReturnNoContent() throws Exception {
        // Arrange
        Mockito.doNothing().when(productService).delete(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/products/1")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
