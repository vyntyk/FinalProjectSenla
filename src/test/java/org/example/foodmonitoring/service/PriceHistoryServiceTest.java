package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.PriceHistoryDTO;
import org.example.foodmonitoring.entity.PriceHistory;
import org.example.foodmonitoring.entity.Product;
import org.example.foodmonitoring.entity.Store;
import org.example.foodmonitoring.exception.ResourceNotFoundException;
import org.example.foodmonitoring.mapper.PriceHistoryMapper;
import org.example.foodmonitoring.repository.PriceHistoryRepository;
import org.example.foodmonitoring.repository.ProductRepository;
import org.example.foodmonitoring.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceHistoryServiceTest {

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private PriceHistoryMapper priceHistoryMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private PriceHistoryService priceHistoryService;

    private Product product;
    private Store store;
    private PriceHistoryDTO priceHistoryDTO;
    private PriceHistory priceHistoryEntity;
    private PriceHistory savedPriceHistoryEntity;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        store = new Store();
        store.setId(1L);
        store.setName("Test Store");

        priceHistoryDTO = new PriceHistoryDTO();
        priceHistoryDTO.setProductId(product.getId());
        priceHistoryDTO.setStoreId(store.getId());
        priceHistoryDTO.setPrice(new BigDecimal("9.99"));
        // Timestamp will be set by the service

        priceHistoryEntity = new PriceHistory();
        priceHistoryEntity.setProduct(product);
        priceHistoryEntity.setStore(store);
        priceHistoryEntity.setPrice(new BigDecimal("9.99"));
        // Timestamp will be set on DTO then mapped

        savedPriceHistoryEntity = new PriceHistory();
        savedPriceHistoryEntity.setId(1L);
        savedPriceHistoryEntity.setProduct(product);
        savedPriceHistoryEntity.setStore(store);
        savedPriceHistoryEntity.setPrice(new BigDecimal("9.99"));
        savedPriceHistoryEntity.setTimestamp(LocalDateTime.now()); // Simulate timestamp being set
    }

    @Test
    void addPrice_shouldAddPriceSuccessfully() {
        // Arrange
        when(productRepository.findById(priceHistoryDTO.getProductId())).thenReturn(Optional.of(product));
        when(storeRepository.findById(priceHistoryDTO.getStoreId())).thenReturn(Optional.of(store));

        // Capture the DTO passed to the mapper to check the timestamp
        ArgumentCaptor<PriceHistoryDTO> dtoCaptor = ArgumentCaptor.forClass(PriceHistoryDTO.class);
        when(priceHistoryMapper.toEntity(dtoCaptor.capture())).thenReturn(priceHistoryEntity);

        when(priceHistoryRepository.save(priceHistoryEntity)).thenReturn(savedPriceHistoryEntity);

        PriceHistoryDTO expectedReturnDTO = new PriceHistoryDTO(); // DTO that mapper.toDto would return
        expectedReturnDTO.setId(savedPriceHistoryEntity.getId());
        expectedReturnDTO.setProductId(savedPriceHistoryEntity.getProduct().getId());
        expectedReturnDTO.setStoreId(savedPriceHistoryEntity.getStore().getId());
        expectedReturnDTO.setPrice(savedPriceHistoryEntity.getPrice());
        expectedReturnDTO.setTimestamp(savedPriceHistoryEntity.getTimestamp());
        when(priceHistoryMapper.toDto(savedPriceHistoryEntity)).thenReturn(expectedReturnDTO);

        // Act
        PriceHistoryDTO result = priceHistoryService.addPrice(priceHistoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedReturnDTO.getId(), result.getId());
        assertEquals(expectedReturnDTO.getPrice(), result.getPrice());
        assertNotNull(result.getTimestamp()); // Ensure timestamp is set in the returned DTO

        // Verify timestamp was set on DTO before mapping
        PriceHistoryDTO capturedDto = dtoCaptor.getValue();
        assertNotNull(capturedDto.getTimestamp());

        verify(productRepository, times(1)).findById(priceHistoryDTO.getProductId());
        verify(storeRepository, times(1)).findById(priceHistoryDTO.getStoreId());
        verify(priceHistoryMapper, times(1)).toEntity(any(PriceHistoryDTO.class)); // any() because timestamp is dynamic
        verify(priceHistoryRepository, times(1)).save(priceHistoryEntity);
        verify(priceHistoryMapper, times(1)).toDto(savedPriceHistoryEntity);
    }

    @Test
    void addPrice_shouldThrowResourceNotFoundException_whenProductIdNotFound() {
        // Arrange
        when(productRepository.findById(priceHistoryDTO.getProductId())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            priceHistoryService.addPrice(priceHistoryDTO);
        });

        assertEquals("Product not found with id: " + priceHistoryDTO.getProductId() + " when adding price", exception.getMessage());

        verify(productRepository, times(1)).findById(priceHistoryDTO.getProductId());
        verify(storeRepository, never()).findById(anyLong());
        verify(priceHistoryMapper, never()).toEntity(any(PriceHistoryDTO.class));
        verify(priceHistoryRepository, never()).save(any(PriceHistory.class));
        verify(priceHistoryMapper, never()).toDto(any(PriceHistory.class));
    }

    @Test
    void addPrice_shouldThrowResourceNotFoundException_whenStoreIdNotFound() {
        // Arrange
        when(productRepository.findById(priceHistoryDTO.getProductId())).thenReturn(Optional.of(product));
        when(storeRepository.findById(priceHistoryDTO.getStoreId())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            priceHistoryService.addPrice(priceHistoryDTO);
        });

        assertEquals("Store not found with id: " + priceHistoryDTO.getStoreId() + " when adding price", exception.getMessage());

        verify(productRepository, times(1)).findById(priceHistoryDTO.getProductId());
        verify(storeRepository, times(1)).findById(priceHistoryDTO.getStoreId());
        verify(priceHistoryMapper, never()).toEntity(any(PriceHistoryDTO.class));
        verify(priceHistoryRepository, never()).save(any(PriceHistory.class));
        verify(priceHistoryMapper, never()).toDto(any(PriceHistory.class));
    }
}
