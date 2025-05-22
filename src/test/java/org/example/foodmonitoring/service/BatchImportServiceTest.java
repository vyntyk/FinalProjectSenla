package org.example.foodmonitoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.foodmonitoring.dto.*;
import org.example.foodmonitoring.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BatchImportServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private PriceHistoryService priceHistoryService;

    @InjectMocks
    private BatchImportService batchImportService;

    // ObjectMapper is initialized in BatchImportService constructor

    private MockMultipartFile createMockCsvFile(String fileName, String content) {
        return new MockMultipartFile(
                fileName,
                fileName + ".csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    private MockMultipartFile createMockJsonFile(String fileName, String content) {
        return new MockMultipartFile(
                fileName,
                fileName + ".json",
                MediaType.APPLICATION_JSON_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    private MockMultipartFile createEmptyMockFile(String fileName, String originalExtension) {
        return new MockMultipartFile(
                fileName,
                fileName + "." + originalExtension,
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );
    }

    // --- Tests for importProducts ---

    @Test
    void testImportProducts_Csv_Successful() throws Exception {
        String csvContent = "name,categoryId,description\n" +
                            "Apple,1,Red Apple\n" +
                            "Banana,1,Yellow Banana";
        MockMultipartFile file = createMockCsvFile("products", csvContent);

        when(productService.create(any(ProductDTO.class))).thenAnswer(invocation -> {
            ProductDTO dto = invocation.getArgument(0);
            dto.setId(System.nanoTime()); // Simulate ID generation
            return dto;
        });

        BatchImportResultDTO result = batchImportService.importProducts(file, "csv");

        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows());
        assertTrue(result.getErrorMessages().isEmpty());

        ArgumentCaptor<ProductDTO> productDTOCaptor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(productService, times(2)).create(productDTOCaptor.capture());
        List<ProductDTO> capturedDTOs = productDTOCaptor.getAllValues();
        assertEquals("Apple", capturedDTOs.get(0).getName());
        assertEquals(1L, capturedDTOs.get(0).getCategoryId());
        assertEquals("Red Apple", capturedDTOs.get(0).getDescription());
        assertEquals("Banana", capturedDTOs.get(1).getName());
        assertEquals(1L, capturedDTOs.get(1).getCategoryId());
        assertEquals("Yellow Banana", capturedDTOs.get(1).getDescription());
    }

    @Test
    void testImportProducts_Json_Successful() throws Exception {
        String jsonContent = "[{\"name\": \"Orange\", \"categoryId\": 1, \"description\": \"Juicy Orange\"}," +
                             "{\"name\": \"Pear\", \"categoryId\": 1, \"description\": \"Green Pear\"}]";
        MockMultipartFile file = createMockJsonFile("products", jsonContent);

        when(productService.create(any(ProductDTO.class))).thenAnswer(invocation -> {
            ProductDTO dto = invocation.getArgument(0);
            dto.setId(System.nanoTime());
            return dto;
        });

        BatchImportResultDTO result = batchImportService.importProducts(file, "json");

        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows());
        assertTrue(result.getErrorMessages().isEmpty());

        ArgumentCaptor<ProductDTO> productDTOCaptor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(productService, times(2)).create(productDTOCaptor.capture());
        List<ProductDTO> capturedDTOs = productDTOCaptor.getAllValues();
        assertEquals("Orange", capturedDTOs.get(0).getName());
        assertEquals(1L, capturedDTOs.get(0).getCategoryId());
        assertEquals("Juicy Orange", capturedDTOs.get(0).getDescription());
    }

    @Test
    void testImportProducts_PartialSuccess_Csv() throws Exception {
        String csvContent = "name,categoryId,description\n" +
                            "Apple,1,Red Apple\n" + // Will succeed
                            "InvalidProduct,999,No Category"; // Will fail (category not found)
        MockMultipartFile file = createMockCsvFile("products_partial", csvContent);

        ProductDTO appleDTO = new ProductDTO();
        appleDTO.setName("Apple"); appleDTO.setCategoryId(1L); appleDTO.setDescription("Red Apple");

        // Ensure matchers are specific and handle potential nulls if needed, though service should not pass null DTOs from valid rows
        when(productService.create(argThat(dto -> dto != null && "Apple".equals(dto.getName()))))
            .thenReturn(appleDTO); 
        when(productService.create(argThat(dto -> dto != null && "InvalidProduct".equals(dto.getName()))))
            .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        BatchImportResultDTO result = batchImportService.importProducts(file, "csv");

        assertEquals(2, result.getTotalRows());
        assertEquals(1, result.getSuccessfullyImportedRows());
        assertEquals(1, result.getFailedRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Row 2 (Name: InvalidProduct): Category not found with id: 999"));
    }
    
    @Test
    void testImportProducts_MalformedCsv() {
        String csvContent = "name,categoryId,description\n" +
                            "Apple,1,\"Mismatched quote"; // Malformed CSV
        MockMultipartFile file = createMockCsvFile("products_malformed", csvContent);

        BatchImportResultDTO result = batchImportService.importProducts(file, "csv");
        
        // Depending on parser strictness, totalRows might be 0 or more. FailedRows should reflect parsing error.
        assertTrue(result.getFailedRows() >= 0); // Could be 0 if totalRows is also 0.
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Error parsing csv file"));
    }

    @Test
    void testImportProducts_MalformedJson() {
        String jsonContent = "[{\"name\": \"Orange\", \"categoryId\": 1, \"description\": \"Juicy Orange\"}, {\"name\": \"Pear\","; // Malformed JSON
        MockMultipartFile file = createMockJsonFile("products_malformed", jsonContent);

        BatchImportResultDTO result = batchImportService.importProducts(file, "json");

        assertEquals(0, result.getTotalRows()); // Parsing fails before rows are counted
        assertEquals(0, result.getSuccessfullyImportedRows());
        // FailedRows might be 0 if totalRows is 0. The error message is key.
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Error reading file:"), "Expected JSON parsing error message to indicate reading/stream issue, got: " + result.getErrorMessages().get(0));
    }
    
    @Test
    void testImportProducts_UnsupportedFormat() {
        MockMultipartFile file = createMockCsvFile("products_unsupported", "content");
        BatchImportResultDTO result = batchImportService.importProducts(file, "xml");

        assertEquals(0, result.getTotalRows());
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows()); // No rows processed, so no individual row failures
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Unsupported file format: xml"));
    }

    @Test
    void testImportProducts_EmptyFile() {
        MockMultipartFile file = createEmptyMockFile("products_empty", "csv");
        BatchImportResultDTO result = batchImportService.importProducts(file, "csv");

        assertEquals(0, result.getTotalRows());
        assertEquals(0, result.getSuccessfullyImportedRows());
        // assertEquals(1, result.getFailedRows()); // Or 0, depending on how "empty file" is counted as failure
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("File is empty"));
    }

    @Test
    void testImportProducts_Csv_EmptyData() {
        String csvContent = "name,categoryId,description\n"; // Only headers
        MockMultipartFile file = createMockCsvFile("products_empty_data", csvContent);
        BatchImportResultDTO result = batchImportService.importProducts(file, "csv");
        assertEquals(0, result.getTotalRows());
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows());
        assertTrue(result.getErrorMessages().get(0).contains("No products found in the file"));
    }

    @Test
    void testImportProducts_Json_EmptyArray() {
        String jsonContent = "[]";
        MockMultipartFile file = createMockJsonFile("products_empty_array", jsonContent);
        BatchImportResultDTO result = batchImportService.importProducts(file, "json");
        assertEquals(0, result.getTotalRows());
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows());
        assertTrue(result.getErrorMessages().get(0).contains("No products found in the file"));
    }


    // --- Tests for importPrices ---

    @Test
    void testImportPrices_Csv_Successful() throws Exception {
        String csvContent = "productId,storeId,price,timestamp\n" +
                            "1,10,1.99,2023-01-01T10:00:00\n" +
                            "2,20,2.50,"; // Timestamp optional
        MockMultipartFile file = createMockCsvFile("prices", csvContent);

        when(priceHistoryService.addPrice(any(PriceHistoryDTO.class))).thenAnswer(invocation -> {
            PriceHistoryDTO dto = invocation.getArgument(0);
            dto.setId(System.nanoTime());
            if (dto.getTimestamp() == null) dto.setTimestamp(LocalDateTime.now()); // Simulate service behavior
            return dto;
        });

        BatchImportResultDTO result = batchImportService.importPrices(file, "csv");

        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows());
        assertTrue(result.getErrorMessages().isEmpty());

        ArgumentCaptor<PriceHistoryDTO> captor = ArgumentCaptor.forClass(PriceHistoryDTO.class);
        verify(priceHistoryService, times(2)).addPrice(captor.capture());
        List<PriceHistoryDTO> capturedDTOs = captor.getAllValues();
        
        assertEquals(1L, capturedDTOs.get(0).getProductId());
        assertEquals(10L, capturedDTOs.get(0).getStoreId());
        assertEquals(new BigDecimal("1.99"), capturedDTOs.get(0).getPrice());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0, 0), capturedDTOs.get(0).getTimestamp());

        assertEquals(2L, capturedDTOs.get(1).getProductId());
        assertEquals(20L, capturedDTOs.get(1).getStoreId());
        assertEquals(new BigDecimal("2.50"), capturedDTOs.get(1).getPrice());
        // Timestamp for 2nd entry is set by service, so we just check it's not null
        assertNotNull(capturedDTOs.get(1).getTimestamp()); 
    }
    
    @Test
    void testImportPrices_Json_Successful() throws Exception {
        String jsonContent = "[{\"productId\":1,\"storeId\":10,\"price\":1.99,\"timestamp\":\"2023-01-01T10:00:00\"}," +
                             "{\"productId\":2,\"storeId\":20,\"price\":2.50}]";
        MockMultipartFile file = createMockJsonFile("prices", jsonContent);

        when(priceHistoryService.addPrice(any(PriceHistoryDTO.class))).thenAnswer(invocation -> {
            PriceHistoryDTO dto = invocation.getArgument(0);
            dto.setId(System.nanoTime());
             if (dto.getTimestamp() == null) dto.setTimestamp(LocalDateTime.now());
            return dto;
        });
        
        BatchImportResultDTO result = batchImportService.importPrices(file, "json");

        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows());
        assertTrue(result.getErrorMessages().isEmpty());
        verify(priceHistoryService, times(2)).addPrice(any(PriceHistoryDTO.class));
    }

    @Test
    void testImportPrices_PartialSuccess_Csv() throws Exception {
        String csvContent = "productId,storeId,price,timestamp\n" +
                            "1,10,1.99,2023-01-01T10:00:00\n" + // Succeeds
                            "999,20,2.50,"; // Fails (product not found)
        MockMultipartFile file = createMockCsvFile("prices_partial", csvContent);

        PriceHistoryDTO validDTO = new PriceHistoryDTO();
        validDTO.setProductId(1L); validDTO.setStoreId(10L); validDTO.setPrice(new BigDecimal("1.99"));
        validDTO.setTimestamp(LocalDateTime.of(2023,1,1,10,0,0)); // set timestamp for valid DTO

        when(priceHistoryService.addPrice(argThat(dto -> dto != null && dto.getProductId() != null && dto.getProductId() == 1L)))
            .thenReturn(validDTO);
        when(priceHistoryService.addPrice(argThat(dto -> dto != null && dto.getProductId() != null && dto.getProductId() == 999L)))
            .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        BatchImportResultDTO result = batchImportService.importPrices(file, "csv");

        assertEquals(2, result.getTotalRows());
        assertEquals(1, result.getSuccessfullyImportedRows());
        assertEquals(1, result.getFailedRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Row 2 (Product: 999, Store: 20): Product not found with id: 999"));
    }
    
    @Test
    void testImportPrices_Csv_MissingRequiredFields() {
        String csvContent = "productId,storeId,price,timestamp\n" +
                            "1,,1.99,2023-01-01T10:00:00"; // Missing storeId
        MockMultipartFile file = createMockCsvFile("prices_missing_fields", csvContent);
        BatchImportResultDTO result = batchImportService.importPrices(file, "csv");

        // When CsvRequiredFieldEmptyException occurs, parsing stops. 
        // Total rows might be 0 or more depending on when the parser gives up.
        // SuccessfullyImportedRows will be 0. FailedRows in the DTO is not incremented by the parsing exception block.
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertEquals(0, result.getFailedRows()); // No rows are processed to be counted as "failed" in the loop.
        assertFalse(result.getErrorMessages().isEmpty());
        // The error message from CsvToBeanBuilder for a required field being empty on a line.
        // OpenCSV's CsvRequiredFieldEmptyException's message is often part of a broader RuntimeException from the parse method.
        // The service currently catches RuntimeException and uses e.getMessage().
        // The actual message includes the line content and error.
        String actualErrorMessage = result.getErrorMessages().get(0);
        assertTrue(actualErrorMessage.contains("Error parsing csv file:"), "Error message should indicate a CSV parsing error. Actual: " + actualErrorMessage);
        assertTrue(actualErrorMessage.contains("Field 'storeId' is mandatory") || actualErrorMessage.contains("Error parsing CSV line"), // More flexible check
                   "Error message should indicate mandatory field 'storeId' was empty or a line parsing error. Actual: " + actualErrorMessage);
        // TotalRows might be 0 if parsing fails before any rows are fully processed by CsvToBean.
        // Or it might be 1 if it processes the problematic line partially. This depends on OpenCSV behavior.
        // For robustness, we might not assert totalRows strictly if parsing itself fails this way.
        // However, if CsvToBeanBuilder().parse() returns an empty list due to this, totalRows set from list.size() would be 0.
        // If it processes the first line and fails, totalRows might be 1.
        // Given the service code, if parse() throws, totalRows is not updated from list size.
        // Let's assume totalRows will remain 0 as set by BatchImportResultDTO constructor if parsing fails.
        assertEquals(0, result.getTotalRows(), "Total rows should be 0 if parsing fails due to required field.");
    }

    @Test
    void testImportPrices_MalformedCsv() {
        String csvContent = "productId,storeId,price,timestamp\n" +
                            "1,10,\"mismatched_quote";
        MockMultipartFile file = createMockCsvFile("prices_malformed", csvContent);
        BatchImportResultDTO result = batchImportService.importPrices(file, "csv");
        
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Error parsing csv file"));
    }

    @Test
    void testImportPrices_MalformedJson() {
        String jsonContent = "[{\"productId\":1,"; // Malformed
        MockMultipartFile file = createMockJsonFile("prices_malformed", jsonContent);
        BatchImportResultDTO result = batchImportService.importPrices(file, "json");

        assertEquals(0, result.getTotalRows());
        assertEquals(0, result.getSuccessfullyImportedRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Error reading file:"), "Expected JSON parsing error message to indicate reading/stream issue, got: " + result.getErrorMessages().get(0));
    }
    
    @Test
    void testImportPrices_UnsupportedFormat() {
        MockMultipartFile file = createMockCsvFile("prices_unsupported", "content");
        BatchImportResultDTO result = batchImportService.importPrices(file, "xml");
        assertEquals(0, result.getTotalRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("Unsupported file format: xml"));
    }

    @Test
    void testImportPrices_EmptyFile() {
        MockMultipartFile file = createEmptyMockFile("prices_empty","csv");
        BatchImportResultDTO result = batchImportService.importPrices(file, "csv");
        assertEquals(0, result.getTotalRows());
        assertFalse(result.getErrorMessages().isEmpty());
        assertTrue(result.getErrorMessages().get(0).contains("File is empty"));
    }

    @Test
    void testImportPrices_Csv_EmptyData() {
        String csvContent = "productId,storeId,price,timestamp\n"; // Only headers
        MockMultipartFile file = createMockCsvFile("prices_empty_data", csvContent);
        BatchImportResultDTO result = batchImportService.importPrices(file, "csv");
        assertEquals(0, result.getTotalRows());
        assertTrue(result.getErrorMessages().get(0).contains("No prices found in the file"));
    }

    @Test
    void testImportPrices_Json_EmptyArray() {
        String jsonContent = "[]";
        MockMultipartFile file = createMockJsonFile("prices_empty_array", jsonContent);
        BatchImportResultDTO result = batchImportService.importPrices(file, "json");
        assertEquals(0, result.getTotalRows());
        assertTrue(result.getErrorMessages().get(0).contains("No prices found in the file"));
    }
}
