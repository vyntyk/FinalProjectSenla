package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.StoreDTO;
import org.example.foodmonitoring.service.StoreService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
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

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenCreateStore_thenStatus200AndTextReturned() throws Exception {
        Mockito.when(storeService.create(any(StoreDTO.class))).thenReturn(new StoreDTO());

        String json = """
    {
      "name": "Test Store",
      "address": "Main St"
    }
    """;

        mockMvc.perform(post("/api/stores")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Торговая точка добавлена"));
    }


//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void whenGetStores_thenReturnInitializedData() throws Exception {
//        StoreDTO dto = new StoreDTO();
//        dto.setId(1L);
//        dto.setName("Shop");
//        dto.setAddress("Street");
//
//        Mockito.when(storeService.listAll()).thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/api/stores"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].name", is("Shop")))
//                .andExpect(jsonPath("$[0].address", is("Street")));
//    }
}