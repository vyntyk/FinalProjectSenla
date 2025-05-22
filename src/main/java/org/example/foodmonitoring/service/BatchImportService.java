package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.BatchImportResultDTO;
import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.dto.PriceHistoryDTO; // Keep for importPrices method
import org.example.foodmonitoring.dto.ProductImportRowDTO;
import org.example.foodmonitoring.dto.PriceImportRowDTO; // Added for importPrices
import org.example.foodmonitoring.exception.ResourceNotFoundException; // For specific exception handling
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.opencsv.bean.CsvToBeanBuilder;
// CsvException is a RuntimeException, so specific catch is optional
// import com.opencsv.exceptions.CsvException; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BatchImportService {

    private final ProductService productService;
    private final PriceHistoryService priceHistoryService; 
    private final ObjectMapper objectMapper;

    public BatchImportService(ProductService productService, PriceHistoryService priceHistoryService) {
        this.productService = productService;
        this.priceHistoryService = priceHistoryService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public BatchImportResultDTO importProducts(MultipartFile file, String format) {
        BatchImportResultDTO result = new BatchImportResultDTO();
        List<ProductImportRowDTO> productsToImport = Collections.emptyList();

        if (file.isEmpty()) {
            result.addErrorMessage("File is empty.");
            return result;
        }

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            if ("csv".equalsIgnoreCase(format)) {
                productsToImport = new CsvToBeanBuilder<ProductImportRowDTO>(bufferedReader)
                        .withType(ProductImportRowDTO.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build()
                        .parse();
            } else if ("json".equalsIgnoreCase(format)) {
                productsToImport = objectMapper.readValue(bufferedReader, new TypeReference<List<ProductImportRowDTO>>() {});
            } else {
                result.addErrorMessage("Unsupported file format: " + format + ". Please use 'csv' or 'json'.");
                return result;
            }
            result.setTotalRows(productsToImport.size());

        } catch (IOException e) {
            result.addErrorMessage("Error reading file: " + e.getMessage());
            return result; 
        } catch (RuntimeException e) { 
            result.addErrorMessage("Error parsing " + format + " file: " + e.getMessage());
            return result;
        }

        if (productsToImport != null && !productsToImport.isEmpty()) {
            for (int i = 0; i < productsToImport.size(); i++) {
                ProductImportRowDTO row = productsToImport.get(i);
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(row.getName());
                productDTO.setCategoryId(row.getCategoryId());
                productDTO.setDescription(row.getDescription());

                try {
                    if (row.getName() == null || row.getName().isBlank()) {
                        throw new IllegalArgumentException("Product name cannot be blank.");
                    }
                    if (row.getCategoryId() == null) {
                        throw new IllegalArgumentException("Category ID cannot be null.");
                    }
                    
                    productService.create(productDTO);
                    result.incrementSuccess();
                } catch (ResourceNotFoundException | IllegalArgumentException e) { 
                    result.incrementFailure();
                    result.addErrorMessage("Row " + (i + 1) + " (Name: " + (row.getName() != null ? row.getName() : "N/A") + "): " + e.getMessage());
                } catch (Exception e) { 
                    result.incrementFailure();
                    result.addErrorMessage("Row " + (i + 1) + " (Name: " + (row.getName() != null ? row.getName() : "N/A") + "): Unexpected error - " + e.getMessage());
                }
            }
        } else if (result.getErrorMessages().isEmpty()) { 
             result.addErrorMessage("No products found in the file or file format issue (e.g., empty JSON array/CSV).");
        }
        return result;
    }

    public BatchImportResultDTO importPrices(MultipartFile file, String format) {
        BatchImportResultDTO result = new BatchImportResultDTO();
        List<PriceImportRowDTO> pricesToImport = new ArrayList<>(); // Initialize to avoid NullPointerException

        if (file.isEmpty()) {
            result.addErrorMessage("File is empty.");
            result.setTotalRows(0); // Default constructor already sets totalRows to 0
            return result;
        }

        format = format.toLowerCase(); // Normalize format string
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            if ("csv".equals(format)) {
                 pricesToImport = new CsvToBeanBuilder<PriceImportRowDTO>(reader)
                    .withType(PriceImportRowDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    // Assuming headers: productId,storeId,price,timestamp
                    // If your PriceImportRowDTO uses @CsvBindByName, headers must match field names.
                    // If using @CsvBindByPosition, ensure correct order.
                    .build()
                    .parse();
            } else if ("json".equals(format)) {
                pricesToImport = objectMapper.readValue(reader, new TypeReference<List<PriceImportRowDTO>>() {});
            } else {
                result.addErrorMessage("Unsupported file format: " + format + ". Please use 'csv' or 'json'.");
                return result;
            }
        } catch (IOException e) {
            result.addErrorMessage("Error reading file: " + e.getMessage());
            return result;
        } catch (Exception e) { // Catches CsvException, JsonProcessingException, other RuntimeExceptions
            result.addErrorMessage("Error parsing " + format + " file: " + e.getMessage());
            return result;
        }

        if (pricesToImport == null || pricesToImport.isEmpty()) { // pricesToImport can be null if parser returns null
            if (result.getErrorMessages().isEmpty()) { // No parsing errors, but no data
                result.addErrorMessage("No prices found in the file or file format issue.");
            }
            result.setTotalRows(0); // Ensure total rows is 0 if list is null or empty
            return result;
        }
        
        result.setTotalRows(pricesToImport.size());

        for (int i = 0; i < pricesToImport.size(); i++) {
            PriceImportRowDTO row = pricesToImport.get(i);
            PriceHistoryDTO priceDTO = new PriceHistoryDTO();
            
            // Basic validation for required fields in PriceImportRowDTO
            if (row.getProductId() == null || row.getStoreId() == null || row.getPrice() == null) {
                result.incrementFailure();
                result.addErrorMessage("Row " + (i + 1) + ": Missing required fields (productId, storeId, price). " +
                                       "ProductId: " + row.getProductId() + ", StoreId: " + row.getStoreId() + ", Price: " + row.getPrice());
                continue; 
            }
            priceDTO.setProductId(row.getProductId());
            priceDTO.setStoreId(row.getStoreId());
            priceDTO.setPrice(row.getPrice());
            if (row.getTimestamp() != null) {
                priceDTO.setTimestamp(row.getTimestamp());
            }
            // If row.getTimestamp() is null, priceHistoryService.addPrice will set it to LocalDateTime.now()

            try {
                priceHistoryService.addPrice(priceDTO);
                result.incrementSuccess();
            } catch (ResourceNotFoundException | IllegalArgumentException e) {
                result.incrementFailure();
                result.addErrorMessage("Row " + (i + 1) + " (Product: " + row.getProductId() + ", Store: " + row.getStoreId() + "): " + e.getMessage());
            } catch (Exception e) {
                result.incrementFailure();
                result.addErrorMessage("Row " + (i + 1) + " (Product: " + row.getProductId() + ", Store: " + row.getStoreId() + "): Unexpected error - " + e.getMessage());
                // Consider logging the full stack trace for 'e' on the server for better diagnostics
            }
        }
        return result;
    }
}
