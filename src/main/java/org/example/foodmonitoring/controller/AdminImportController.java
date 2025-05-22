package org.example.foodmonitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.foodmonitoring.dto.BatchImportResultDTO;
import org.example.foodmonitoring.service.BatchImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Административное Управление Импортом", description = "API для пакетного импорта данных (только для администраторов)")
@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "JWT") // Global security requirement for the controller
public class AdminImportController {

    private final BatchImportService batchImportService;

    public AdminImportController(BatchImportService batchImportService) {
        this.batchImportService = batchImportService;
    }

    @PostMapping("/products/batch-import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
        summary = "Пакетный импорт продуктов",
        description = "Загрузка CSV или JSON файла для пакетного создания продуктов. Доступно только для администраторов.",
        parameters = {
            @Parameter(name = "file", description = "Файл с данными продуктов (CSV или JSON)", required = true, content = @Content(mediaType = "application/octet-stream")),
            @Parameter(name = "format", description = "Формат файла ('csv' или 'json')", required = true, example = "csv")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Импорт завершен (возможно с ошибками)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BatchImportResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла или некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (необходима роль ADMIN)")
        },
        tags = {"Admin Batch Import"}
    )
    public ResponseEntity<BatchImportResultDTO> importProducts(
            @RequestPart("file") MultipartFile file,
            @RequestParam("format") String format) {
        if (file.isEmpty()) {
            BatchImportResultDTO result = new BatchImportResultDTO();
            result.addErrorMessage("Файл не может быть пустым.");
            result.setTotalRows(0);
            return ResponseEntity.badRequest().body(result);
        }
        BatchImportResultDTO result = batchImportService.importProducts(file, format.toLowerCase());
        if (result.getFailedRows() > 0 && result.getSuccessfullyImportedRows() == 0 && result.getTotalRows() > 0 ) {
             // If all rows failed from an otherwise valid file processing attempt
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/prices/batch-import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
        summary = "Пакетный импорт цен",
        description = "Загрузка CSV или JSON файла для пакетного добавления цен на продукты. Доступно только для администраторов.",
        parameters = {
            @Parameter(name = "file", description = "Файл с данными цен (CSV или JSON)", required = true, content = @Content(mediaType = "application/octet-stream")),
            @Parameter(name = "format", description = "Формат файла ('csv' или 'json')", required = true, example = "csv")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Импорт завершен (возможно с ошибками)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BatchImportResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла или некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (необходима роль ADMIN)")
        },
        tags = {"Admin Batch Import"} 
    )
    public ResponseEntity<BatchImportResultDTO> importPrices(
            @RequestPart("file") MultipartFile file,
            @RequestParam("format") String format) {
        if (file.isEmpty()) {
            BatchImportResultDTO result = new BatchImportResultDTO();
            result.addErrorMessage("Файл не может быть пустым.");
            result.setTotalRows(0);
            return ResponseEntity.badRequest().body(result);
        }
        BatchImportResultDTO result = batchImportService.importPrices(file, format.toLowerCase());
        if (result.getFailedRows() > 0 && result.getSuccessfullyImportedRows() == 0 && result.getTotalRows() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
