package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.entity.Store;
import org.example.foodmonitoring.security.JwtTokenProvider;
import org.example.foodmonitoring.service.StoreService;
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

@WebMvcTest(controllers = StoreController.class)
@Import(org.example.foodmonitoring.config.TestSecurityConfig.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenGetAllStores_thenReturnStoresList() throws Exception {
        // Arrange
        Store store = new Store();
        store.setId(1L);
        store.setName("Test Store");
        store.setAddress("Test Address");

        Mockito.when(storeService.getAllStores()).thenReturn(List.of(store));

        // Act & Assert
        mockMvc.perform(get("/api/stores"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Store")))
                .andExpect(jsonPath("$[0].address", is("Test Address")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAddStore_thenReturnSuccessMessage() throws Exception {
        // Arrange
        Mockito.doNothing().when(storeService).addStore(anyString(), anyString());

        String json = """
        {
          "name": "Test Store",
          "address": "Test Address"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/stores")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Торговая точка добавлена"));
    }
}
