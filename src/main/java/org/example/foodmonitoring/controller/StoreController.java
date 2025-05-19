package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.entity.Store;
import org.example.foodmonitoring.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Контроллер магазинов", description = "Контроллер для управления магазинами")
@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @SecurityRequirement(name = "JWT")
    @GetMapping
    @Operation(
            summary = "Список торговых точек",
            description = "Получение списка торговых точек",
            tags = {"Stores"}
    )
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @SecurityRequirement(name = "JWT")
    @PostMapping
    @Operation(
            summary = "Добавление торговой точки",
            description = "Добавление новой торговой точки",
            tags = {"Stores"}
    )
    public ResponseEntity<String> addStore(@RequestBody Store store) {
        storeService.addStore(store.getName(), store.getAddress());
        return ResponseEntity.ok("Торговая точка добавлена");
    }
}