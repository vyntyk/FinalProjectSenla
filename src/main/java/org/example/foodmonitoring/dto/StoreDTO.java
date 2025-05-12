package org.example.foodmonitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {

    @Schema(description = "Идентификатор магазина", example = "1")
    private Long id;

    @Schema(description = "Название магазина", example = "Магазин 1")
    @NotBlank
    private String name;

    @Schema(description = "Адрес магазина", example = "г. Москва, ул. Примерная, д. 1")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



}
