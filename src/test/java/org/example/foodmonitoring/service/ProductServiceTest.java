package org.example.foodmonitoring.service;

import org.example.foodmonitoring.dto.ProductDTO;
import org.example.foodmonitoring.entity.Product;
import org.example.foodmonitoring.exception.ResourceNotFoundException;
import org.example.foodmonitoring.mapper.ProductMapper;
import org.example.foodmonitoring.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product DTO");
        productDTO.setCategoryId(10L); // Assuming a category ID
    }

    @Test
    void list_shouldCallRepositoryFindAllAndMapResults() {
        // Arrange
        String nameFilter = "Test";
        Long categoryIdFilter = 1L;
        List<Product> productsFromRepo = Collections.singletonList(product);
        when(productRepository.findAll(nameFilter, categoryIdFilter)).thenReturn(productsFromRepo);
        when(productMapper.toDto(product)).thenReturn(productDTO);

        // Act
        List<ProductDTO> result = productService.list(nameFilter, categoryIdFilter);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO.getName(), result.get(0).getName());
        verify(productRepository, times(1)).findAll(nameFilter, categoryIdFilter);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    void create_shouldCreateProductSuccessfully() {
        // Arrange
        Product savedProduct = new Product(); // Simulate a saved product (could be same as 'product' or different)
        savedProduct.setId(1L);
        
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDto(savedProduct)).thenReturn(productDTO); // Mapper returns the DTO for the saved product

        // Act
        ProductDTO result = productService.create(productDTO);

        // Assert
        assertNotNull(result);
        assertEquals(productDTO.getId(), result.getId());
        verify(productMapper, times(1)).toEntity(productDTO);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toDto(savedProduct);
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenCategoryIsInvalid() {
        // Arrange
        when(productMapper.toEntity(productDTO)).thenThrow(new ResourceNotFoundException("Category not found"));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.create(productDTO);
        });
        assertEquals("Category not found", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_shouldUpdateProductSuccessfully() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(productDTO.getId());

        Product productToUpdate = new Product(); // This is what mapper.toEntity would return
        productToUpdate.setId(productDTO.getId());
        productToUpdate.setName(productDTO.getName());
        // Assume category is set correctly by the mapper or is part of productToUpdate

        Product updatedProductInRepo = new Product(); // This is what repo.save would return
        updatedProductInRepo.setId(productDTO.getId());
        updatedProductInRepo.setName(productDTO.getName());


        when(productRepository.findById(productDTO.getId())).thenReturn(Optional.of(existingProduct));
        when(productMapper.toEntity(productDTO)).thenReturn(productToUpdate);
        when(productRepository.save(productToUpdate)).thenReturn(updatedProductInRepo);
        when(productMapper.toDto(updatedProductInRepo)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.update(productDTO);

        // Assert
        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        verify(productRepository, times(1)).findById(productDTO.getId());
        verify(productMapper, times(1)).toEntity(productDTO);
        verify(productRepository, times(1)).save(productToUpdate);
        verify(productMapper, times(1)).toDto(updatedProductInRepo);
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenProductIdNotFound() {
        // Arrange
        when(productRepository.findById(productDTO.getId())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(productDTO);
        });
        assertEquals("Product not found with id: " + productDTO.getId(), exception.getMessage());
        verify(productMapper, never()).toEntity(any(ProductDTO.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenCategoryIsInvalid() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(productDTO.getId());
        when(productRepository.findById(productDTO.getId())).thenReturn(Optional.of(existingProduct));
        when(productMapper.toEntity(productDTO)).thenThrow(new ResourceNotFoundException("Category not found"));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(productDTO);
        });
        assertEquals("Category not found", exception.getMessage());
        verify(productRepository, times(1)).findById(productDTO.getId()); // findById is called
        verify(productMapper, times(1)).toEntity(productDTO); // toEntity is called
        verify(productRepository, never()).save(any(Product.class)); // save is not called
    }
    
    @Test
    void delete_shouldDeleteProductSuccessfully() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).deleteById(productId);

        // Act
        productService.delete(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenProductIdNotFound() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(productId);
        });
        assertEquals("Product not found with id: " + productId, exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).deleteById(productId);
    }
}
