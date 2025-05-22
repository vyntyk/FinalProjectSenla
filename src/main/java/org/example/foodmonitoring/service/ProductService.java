package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.entity.Product;
import org.example.foodmonitoring.exception.ResourceNotFoundException; // Added import
import org.example.foodmonitoring.mapper.ProductMapper;
import org.example.foodmonitoring.repository.ProductRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository repo;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repo, ProductMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public ProductDTO create(ProductDTO dto) {
        Product p = mapper.toEntity(dto);
        return mapper.toDto(repo.save(p));
    }

    public List<ProductDTO> list(String name, Long categoryId) { // Signature changed
        return repo.findAll(name, categoryId) // Call changed
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO update(ProductDTO dto) {
        // Ensure product exists before attempting to map and save
        repo.findById(dto.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getId()));

        Product productToUpdate = mapper.toEntity(dto);
        // The ID is already set on the DTO by the controller, so productToUpdate will have it.
        // ProductRepository.save will perform a merge because ID is not null.
        return mapper.toDto(repo.save(productToUpdate));
    }

    @Transactional
    public void delete(Long id) {
        repo.findById(id) // Check if product exists
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        repo.deleteById(id);
    }
}
