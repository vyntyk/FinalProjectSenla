package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.StoreDTO;
import org.example.foodmonitoring.entity.Store;
import org.example.foodmonitoring.mapper.StoreMapper;
import org.example.foodmonitoring.repository.StoreRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreService {
    private final StoreRepository repo;
    private final StoreMapper mapper;

    public StoreService(StoreRepository repo, StoreMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public StoreDTO create(StoreDTO dto) {
        Store e = mapper.toEntity(dto);
        return mapper.toDto(repo.save(e));
    }

    public List<StoreDTO> listAll() {
        return repo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreDTO update(StoreDTO dto) {
        Store e = mapper.toEntity(dto);
        return mapper.toDto(repo.save(e));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Возвращает все сущности Store
     */
    public List<Store> getAllStores() {
        return repo.findAll();
    }

    /**
     * Добавляет новый магазин с указанными именем и адресом
     */
    @Transactional
    public void addStore(String name, String address) {
        Store store = new Store();
        store.setName(name);
        store.setAddress(address);
        repo.save(store);
    }
}
