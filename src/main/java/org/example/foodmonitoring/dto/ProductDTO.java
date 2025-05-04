package org.example.foodmonitoring.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductDTO {
    private Long id;

    @NotBlank
    private String name;

    private String category;
    private String description;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
