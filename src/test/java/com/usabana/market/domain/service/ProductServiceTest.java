package com.usabana.market.domain.service;

import com.usabana.market.domain.Product;
import com.usabana.market.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // Create first product using builder
        product = Product.builder()
                .productId(1)
                .name("Test Product")
                .categoryId(1)
                .price(100.0)
                .stock(10)
                .active(true)
                .build();

        // Create second product using builder
        Product anotherProduct = Product.builder()
                .productId(2)
                .name("Another Product")
                .categoryId(1)
                .price(150.0)
                .stock(5)
                .active(true)
                .build();

        productList = Arrays.asList(product, anotherProduct);
    }

    @Test
    void getAll_ShouldReturnAllProducts() {
        // Given
        when(productRepository.getAll()).thenReturn(productList);

        // When
        List<Product> result = productService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).getAll();
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() {
        // Given
        int productId = 1;
        when(productRepository.getProduct(productId)).thenReturn(Optional.of(product));

        // When
        Optional<Product> result = productService.getProduct(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getProductId());
        verify(productRepository, times(1)).getProduct(productId);
    }

    @Test
    void getProduct_WhenProductNotExists_ShouldReturnEmpty() {
        // Given
        int productId = 999;
        when(productRepository.getProduct(productId)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.getProduct(productId);

        // Then
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).getProduct(productId);
    }

    @Test
    void getByCategory_WhenCategoryHasProducts_ShouldReturnProducts() {
        // Given
        int categoryId = 1;
        when(productRepository.getByCategory(categoryId)).thenReturn(Optional.of(productList));

        // When
        Optional<List<Product>> result = productService.getByCategory(categoryId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertTrue(result.get().stream().allMatch(p -> p.getCategoryId() == categoryId));
        verify(productRepository, times(1)).getByCategory(categoryId);
    }

    @Test
    void getByCategory_WhenNoProductsInCategory_ShouldReturnEmpty() {
        // Given
        int categoryId = 999;
        when(productRepository.getByCategory(categoryId)).thenReturn(Optional.empty());

        // When
        Optional<List<Product>> result = productService.getByCategory(categoryId);

        // Then
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).getByCategory(categoryId);
    }

    @Test
    void save_ShouldReturnSavedProduct() {
        // Given
        when(productRepository.save(product)).thenReturn(product);

        // When
        Product result = productService.save(product);

        // Then
        assertNotNull(result);
        assertEquals(product.getProductId(), result.getProductId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void delete_WhenProductExists_ShouldReturnTrue() {
        // Given
        int productId = 1;
        when(productRepository.getProduct(productId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(productId);

        // When
        boolean result = productService.delete(productId);

        // Then
        assertTrue(result);
        verify(productRepository, times(1)).getProduct(productId);
        verify(productRepository, times(1)).delete(productId);
    }

    @Test
    void delete_WhenProductNotExists_ShouldReturnFalse() {
        // Given
        int productId = 999;
        when(productRepository.getProduct(productId)).thenReturn(Optional.empty());

        // When
        boolean result = productService.delete(productId);

        // Then
        assertFalse(result);
        verify(productRepository, times(1)).getProduct(productId);
        verify(productRepository, never()).delete(anyInt());
    }
}
