package org.example.foodmonitoring.entity;

import jakarta.persistence.*;
import org.example.foodmonitoring.entity.Category; // Added import

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = true) // Added annotation
    @JoinColumn(name = "category_id") // Added annotation
    private Category category; // Changed type from String to Category

    private String description;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; } // Changed return type
    public void setCategory(Category category) { this.category = category; } // Changed parameter type

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
