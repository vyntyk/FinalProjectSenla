package org.example.foodmonitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Added import

public class ProductDTO {

    @Schema(description = "Идентификатор продукта", example = "1")
    private Long id;

    @Schema(description = "Название продукта", example = "Шоколад")
    @NotBlank
    private String name;

    @Schema(description = "Идентификатор категории", example = "1") // Updated Schema
    @NotNull(message = "Category ID cannot be null") // Added NotNull
    private Long categoryId; // Renamed and changed type

    @Schema(description = "Описание продукта", example = "Шоколадный батон")
    private String description;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCategoryId() { return categoryId; } // Updated getter
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; } // Updated setter

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
