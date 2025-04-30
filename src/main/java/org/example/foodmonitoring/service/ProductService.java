package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.entity.Product;
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

    public List<ProductDTO> list(String name, String category) {
        return repo.findAll(name, category)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO update(ProductDTO dto) {
        Product p = mapper.toEntity(dto);
        return mapper.toDto(repo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
