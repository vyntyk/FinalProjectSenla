package org.example.foodmonitoring.mapper;

import org.example.foodmonitoring.dto.StoreDTO;
import org.example.foodmonitoring.entity.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    Store toEntity(StoreDTO dto);
    StoreDTO toDto(Store entity);
}
