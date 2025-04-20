package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.PriceHistoryDTO;
import org.example.foodmonitoring.entity.PriceHistory;
import org.example.foodmonitoring.mapper.PriceHistoryMapper;
import org.example.foodmonitoring.repository.PriceHistoryRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceHistoryService {
    private final PriceHistoryRepository repo;
    private final PriceHistoryMapper mapper;

    public PriceHistoryService(PriceHistoryRepository repo, PriceHistoryMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public PriceHistoryDTO addPrice(PriceHistoryDTO dto) {
        // timestamp присваиваем здесь
        dto.setTimestamp(LocalDateTime.now());
        PriceHistory ph = mapper.toEntity(dto);
        return mapper.toDto(repo.save(ph));
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
