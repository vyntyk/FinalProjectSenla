package org.example.foodmonitoring.service;

import org.example.foodmonitoring.entity.Store;
import org.example.foodmonitoring.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store addStore(String name, String address) {
        Store store = new Store();
        store.setName(name);
        store.setAddress(address);
        return storeRepository.save(store);
    }
}