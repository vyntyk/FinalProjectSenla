package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.dto.PriceHistoryDTO;
import org.example.foodmonitoring.service.PriceHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Контроллер цены", description = "Контроллер для работы с ценами")
@RestController
@RequestMapping("/prices")
@Validated
public class PriceController {

    private final PriceHistoryService service;

    public PriceController(PriceHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PriceHistoryDTO> add(@RequestBody @Validated PriceHistoryDTO dto) {
        return ResponseEntity.ok(service.addPrice(dto));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PriceHistoryDTO>> history(
            @RequestParam Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(service.history(productId, from, to));
    }

    @GetMapping("/compare")
    public ResponseEntity<List<PriceHistoryDTO>> compare(
            @RequestParam Long productId,
            @RequestParam List<Long> storeIds) {
        return ResponseEntity.ok(service.compare(productId, storeIds));
    }
}
