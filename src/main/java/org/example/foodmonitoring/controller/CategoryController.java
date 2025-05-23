package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.entity.Category;
import org.example.foodmonitoring.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Категории", description = "Контроллер для работы с категориями")
@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @SecurityRequirement(name = "JWT")
    @GetMapping
    @Operation(
            summary = "Получить все категории",
            description = "Получает все категории продуктов",
            tags = {"Categories"}
    )

    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @SecurityRequirement(name = "JWT")
    @PostMapping
    @Operation(
            summary = "Добавить категорию",
            description = "Добавляет новую категорию продуктов",
            tags = {"Categories"}
    )
    public ResponseEntity<String> addCategory(@RequestBody String name) {
        categoryService.addCategory(name);
        return ResponseEntity.ok("Категория добавлена");
    }
}