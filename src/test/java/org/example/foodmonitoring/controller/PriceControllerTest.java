package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.dto.PriceHistoryDTO;
import org.example.foodmonitoring.service.PriceHistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;

@WebMvcTest(PriceController.class)
@Import(org.example.foodmonitoring.config.TestSecurityConfig.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceHistoryService priceHistoryService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAddPrice_thenReturnAddedPrice() throws Exception {
        // Arrange
        PriceHistoryDTO dto = new PriceHistoryDTO();
        dto.setId(1L);
        dto.setProductId(1L);
        dto.setStoreId(1L);
        dto.setPrice(new BigDecimal("9.99"));
        dto.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));

        Mockito.when(priceHistoryService.addPrice(any(PriceHistoryDTO.class))).thenReturn(dto);

        String json = """
        {
          "productId": 1,
          "storeId": 1,
          "price": 9.99
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/prices")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.storeId", is(1)))
                .andExpect(jsonPath("$.price", is(9.99)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenGetHistory_thenReturnPriceHistory() throws Exception {
        // Arrange
        PriceHistoryDTO dto = new PriceHistoryDTO();
        dto.setId(1L);
        dto.setProductId(1L);
        dto.setStoreId(1L);
        dto.setPrice(new BigDecimal("9.99"));
        dto.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));

        Mockito.when(priceHistoryService.history(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
               .thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/prices/history")
                .param("productId", "1")
                .param("from", "2023-01-01T00:00:00")
                .param("to", "2023-01-02T00:00:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].productId", is(1)))
                .andExpect(jsonPath("$[0].storeId", is(1)))
                .andExpect(jsonPath("$[0].price", is(9.99)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenCompare_thenReturnComparisonResults() throws Exception {
        // Arrange
        PriceHistoryDTO dto1 = new PriceHistoryDTO();
        dto1.setId(1L);
        dto1.setProductId(1L);
        dto1.setStoreId(1L);
        dto1.setPrice(new BigDecimal("9.99"));
        dto1.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));

        PriceHistoryDTO dto2 = new PriceHistoryDTO();
        dto2.setId(2L);
        dto2.setProductId(1L);
        dto2.setStoreId(2L);
        dto2.setPrice(new BigDecimal("8.99"));
        dto2.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));

        Mockito.when(priceHistoryService.compare(anyLong(), anyList()))
               .thenReturn(List.of(dto1, dto2));

        // Act & Assert
        mockMvc.perform(get("/prices/compare")
                .param("productId", "1")
                .param("storeIds", "1,2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].productId", is(1)))
                .andExpect(jsonPath("$[0].storeId", is(1)))
                .andExpect(jsonPath("$[0].price", is(9.99)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].productId", is(1)))
                .andExpect(jsonPath("$[1].storeId", is(2)))
                .andExpect(jsonPath("$[1].price", is(8.99)));
    }
}
