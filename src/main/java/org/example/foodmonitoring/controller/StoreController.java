package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.entity.Store;
import org.example.foodmonitoring.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @PostMapping
    public ResponseEntity<String> addStore(@RequestBody Store store) {
        storeService.addStore(store.getName(), store.getAddress());
        return ResponseEntity.ok("Торговая точка добавлена");
    }
}