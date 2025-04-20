package org.example.foodmonitoring.mapper;

import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductDTO dto);
    ProductDTO toDto(Product entity);
}
