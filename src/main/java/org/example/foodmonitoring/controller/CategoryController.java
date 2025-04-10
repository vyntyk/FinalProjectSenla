package org.example.foodmonitoring.controller;

import org.example.foodmonitoring.entity.Category;
import org.example.foodmonitoring.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody String name) {
        categoryService.addCategory(name);
        return ResponseEntity.ok("Категория добавлена");
    }
}