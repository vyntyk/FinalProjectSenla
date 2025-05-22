package org.example.foodmonitoring.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.opencsv.bean.CsvBindByName; // Added import
import com.opencsv.bean.CsvDate;      // Added import

public class PriceImportRowDTO {
    @NotNull(message = "Product ID cannot be null")
    @CsvBindByName(column = "productId", required = true)
    private Long productId;

    @NotNull(message = "Store ID cannot be null")
    @CsvBindByName(column = "storeId", required = true)
    private Long storeId;

    @NotNull(message = "Price cannot be null")
    @CsvBindByName(column = "price", required = true)
    private BigDecimal price;

    @CsvBindByName(column = "timestamp", required = false) 
    @CsvDate("yyyy-MM-dd'T'HH:mm:ss")    
    private LocalDateTime timestamp; 

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
