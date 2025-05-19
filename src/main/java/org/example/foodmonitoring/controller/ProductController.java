package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Контроллер продуктов", description = "Контроллер продуктов")
@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/products")
@Validated
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @SecurityRequirement(name = "JWT")
    @PostMapping
    @Operation(
            summary = "Создание продуктов",
            description = "Создание продуктов в списке",
            tags = {"Products"}
    )
    public ResponseEntity<ProductDTO> create(@RequestBody @Validated ProductDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @SecurityRequirement(name = "JWT")
    @GetMapping
    @Operation(
            summary = "Получение списка продуктов",
            description = "Получение списка продуктов с возможностью фильтрации по имени и категории",
            tags = {"Products"}
    )
    public ResponseEntity<List<ProductDTO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(service.list(name, category));
    }

    @SecurityRequirement(name = "JWT")
    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление продуктов",
            description = "Обновление продуктов в списке",
            tags = {"Products"}
    )
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @RequestBody @Validated ProductDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.update(dto));
    }

    @SecurityRequirement(name = "JWT")

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
