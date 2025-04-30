package org.example.foodmonitoring.mapper;

import org.example.foodmonitoring.dto.PriceHistoryDTO;
import org.example.foodmonitoring.entity.PriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceHistoryMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "store.id",   target = "storeId")
    PriceHistoryDTO toDto(PriceHistory entity);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "storeId",   target = "store.id")
    PriceHistory toEntity(PriceHistoryDTO dto);
}
