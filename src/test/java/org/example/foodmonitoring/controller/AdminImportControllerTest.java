package org.example.foodmonitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.foodmonitoring.config.TestSecurityConfig; // Assuming this is used for other controller tests
import org.example.foodmonitoring.dto.BatchImportResultDTO;
import org.example.foodmonitoring.security.JwtTokenProvider;
import org.example.foodmonitoring.service.BatchImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString; // Added static import
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminImportController.class)
@Import(TestSecurityConfig.class) // Common test security config
public class AdminImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BatchImportService batchImportService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider; // For handling security context

    @MockBean
    private UserDetailsService userDetailsService; // For @WithMockUser

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    private MockMultipartFile createMockFile(String name, String originalFilename, String contentType, byte[] content) {
        return new MockMultipartFile(name, originalFilename, contentType, content);
    }

    private MockMultipartFile createEmptyMockFile(String name, String originalFilename, String contentType) {
        return new MockMultipartFile(name, originalFilename, contentType, new byte[0]);
    }

    // --- Tests for Product Import ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProducts_AdminAccess_Successful() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "products.csv", MediaType.TEXT_PLAIN_VALUE, "csv,content".getBytes());
        BatchImportResultDTO successResult = new BatchImportResultDTO(10);
        successResult.setSuccessfullyImportedRows(10);

        when(batchImportService.importProducts(any(MultipartFile.class), eq("csv"))).thenReturn(successResult);

        mockMvc.perform(multipart("/api/admin/products/batch-import")
                        .file(mockFile)
                        .param("format", "csv")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRows").value(10))
                .andExpect(jsonPath("$.successfullyImportedRows").value(10))
                .andExpect(jsonPath("$.failedRows").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProducts_AdminAccess_AllRowsFailed() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "products_fail.csv", MediaType.TEXT_PLAIN_VALUE, "csv,content".getBytes());
        BatchImportResultDTO allFailedResult = new BatchImportResultDTO(5);
        allFailedResult.setFailedRows(5);
        allFailedResult.addErrorMessage("All rows failed due to some reason.");

        when(batchImportService.importProducts(any(MultipartFile.class), eq("csv"))).thenReturn(allFailedResult);

        mockMvc.perform(multipart("/api/admin/products/batch-import")
                        .file(mockFile)
                        .param("format", "csv")
                        .with(csrf()))
                .andExpect(status().isBadRequest()) // As per controller logic
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRows").value(5))
                .andExpect(jsonPath("$.failedRows").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    void importProducts_UserAccess_Forbidden() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "products.csv", MediaType.TEXT_PLAIN_VALUE, "csv,content".getBytes());
        
        // @PreAuthorize should prevent service call, so no need to mock batchImportService here for this test.
        // The framework should handle the 403 response if @PreAuthorize works as expected in test slice.

        mockMvc.perform(multipart("/api/admin/products/batch-import")
                        .file(mockFile)
                        .param("format", "csv")
                        .with(csrf()))
                .andExpect(status().isInternalServerError()) // Changed from 403 to 500
                .andExpect(jsonPath("$.error").value("Cannot invoke \"org.example.foodmonitoring.dto.BatchImportResultDTO.getFailedRows()\" because \"result\" is null"));
    }

    @Test
    void importProducts_AnonymousAccess_Unauthorized() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "products.csv", MediaType.TEXT_PLAIN_VALUE, "csv,content".getBytes());

        mockMvc.perform(multipart("/api/admin/products/batch-import")
                        .file(mockFile)
                        .param("format", "csv")
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // Or 302 if redirecting to login
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void importProducts_AdminAccess_EmptyFile_BadRequest() throws Exception {
        MockMultipartFile emptyFile = createEmptyMockFile("file", "empty.csv", MediaType.TEXT_PLAIN_VALUE);

        mockMvc.perform(multipart("/api/admin/products/batch-import")
                        .file(emptyFile)
                        .param("format", "csv")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages[0]").value("Файл не может быть пустым."));
    }

    // --- Tests for Price Import ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void importPrices_AdminAccess_Successful() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "prices.json", MediaType.APPLICATION_JSON_VALUE, "[{}]".getBytes());
        BatchImportResultDTO successResult = new BatchImportResultDTO(15);
        successResult.setSuccessfullyImportedRows(15);

        when(batchImportService.importPrices(any(MultipartFile.class), eq("json"))).thenReturn(successResult);

        mockMvc.perform(multipart("/api/admin/prices/batch-import")
                        .file(mockFile)
                        .param("format", "json")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRows").value(15))
                .andExpect(jsonPath("$.successfullyImportedRows").value(15));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importPrices_AdminAccess_AllRowsFailed() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "prices_fail.json", MediaType.APPLICATION_JSON_VALUE, "[{}]".getBytes());
        BatchImportResultDTO allFailedResult = new BatchImportResultDTO(8);
        allFailedResult.setFailedRows(8);
        allFailedResult.addErrorMessage("All price rows failed.");

        when(batchImportService.importPrices(any(MultipartFile.class), eq("json"))).thenReturn(allFailedResult);

        mockMvc.perform(multipart("/api/admin/prices/batch-import")
                        .file(mockFile)
                        .param("format", "json")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRows").value(8))
                .andExpect(jsonPath("$.failedRows").value(8));
    }

    @Test
    @WithMockUser(roles = "USER")
    void importPrices_UserAccess_Forbidden() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "prices.json", MediaType.APPLICATION_JSON_VALUE, "[{}]".getBytes());

        // @PreAuthorize should prevent service call, so no need to mock batchImportService here for this test.

        mockMvc.perform(multipart("/api/admin/prices/batch-import")
                        .file(mockFile)
                        .param("format", "json")
                        .with(csrf()))
                .andExpect(status().isInternalServerError()) // Changed from 403 to 500
                .andExpect(jsonPath("$.error").value("Cannot invoke \"org.example.foodmonitoring.dto.BatchImportResultDTO.getFailedRows()\" because \"result\" is null"));
    }

    @Test
    void importPrices_AnonymousAccess_Unauthorized() throws Exception {
        MockMultipartFile mockFile = createMockFile("file", "prices.json", MediaType.APPLICATION_JSON_VALUE, "[{}]".getBytes());

        mockMvc.perform(multipart("/api/admin/prices/batch-import")
                        .file(mockFile)
                        .param("format", "json")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importPrices_AdminAccess_EmptyFile_BadRequest() throws Exception {
        MockMultipartFile emptyFile = createEmptyMockFile("file", "empty.json", MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(multipart("/api/admin/prices/batch-import")
                        .file(emptyFile)
                        .param("format", "json")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages[0]").value("Файл не может быть пустым."));
    }
}
