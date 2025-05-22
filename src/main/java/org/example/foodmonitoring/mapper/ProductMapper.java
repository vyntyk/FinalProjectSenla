package org.example.foodmonitoring.mapper;

import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.entity.Product;
import org.example.foodmonitoring.entity.Category;
import org.example.foodmonitoring.exception.ResourceNotFoundException;
import org.example.foodmonitoring.repository.CategoryRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Mapping(target = "category", ignore = true) // Ignore direct mapping for category, handle in @AfterMapping
    public abstract Product toEntity(ProductDTO dto);

    @Mapping(source = "category.id", target = "categoryId")
    public abstract ProductDTO toDto(Product entity);

    @AfterMapping
    protected void mapCategory(ProductDTO dto, @MappingTarget Product product) {
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
    }
}
