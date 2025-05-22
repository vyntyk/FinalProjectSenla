package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.PriceHistoryDTO;
import org.example.foodmonitoring.entity.PriceHistory;
import org.example.foodmonitoring.exception.ResourceNotFoundException; // Added import
import org.example.foodmonitoring.mapper.PriceHistoryMapper;
import org.example.foodmonitoring.repository.PriceHistoryRepository;
import org.example.foodmonitoring.repository.ProductRepository; // Added import
import org.example.foodmonitoring.repository.StoreRepository; // Added import
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceHistoryService {
    private final PriceHistoryRepository repo;
    private final PriceHistoryMapper mapper;
    private final ProductRepository productRepository; // Added field
    private final StoreRepository storeRepository; // Added field

    public PriceHistoryService(PriceHistoryRepository repo, PriceHistoryMapper mapper,
                               ProductRepository productRepository, StoreRepository storeRepository) { // Updated constructor
        this.repo = repo;
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public PriceHistoryDTO addPrice(PriceHistoryDTO dto) {
        // Validate product existence
        productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId() + " when adding price"));

        // Validate store existence
        storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + dto.getStoreId() + " when adding price"));

        // timestamp присваиваем здесь
        dto.setTimestamp(LocalDateTime.now());
        PriceHistory ph = mapper.toEntity(dto); // Mapper should now correctly map productId and storeId to entities
        ph = repo.save(ph); // repo.save will persist the PriceHistory entity
        return mapper.toDto(ph);
    }

    public List<PriceHistoryDTO> history(Long productId, LocalDateTime from, LocalDateTime to) {
        return repo.findByProductAndPeriod(productId, from, to)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PriceHistoryDTO> compare(Long productId, List<Long> storeIds) {
        return repo.findLatestByProductAndStores(productId, storeIds)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
