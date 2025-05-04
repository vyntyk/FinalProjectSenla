package org.example.foodmonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    // getters/setters
    private Long id;
    @NotBlank
    private String name;
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
