package org.example.foodmonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductImportRowDTO {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;

    private String description;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
