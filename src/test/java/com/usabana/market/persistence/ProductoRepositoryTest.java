package com.usabana.market.persistence;

import com.usabana.market.domain.Product;
import com.usabana.market.persistence.crud.ProductoCrudRepository;
import com.usabana.market.persistence.entity.Producto;
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
class ProductoRepositoryTest {

    @Mock
    private ProductoCrudRepository productoCrudRepository;

    @InjectMocks
    private ProductoRepository productoRepository;

    private Producto productoEntity;
    private Product productDomain;
    private final int PRODUCT_ID = 1;
    private final int CATEGORY_ID = 1;

    @BeforeEach
    void setUp() {
        productoEntity = Producto.builder()
                .idProducto(PRODUCT_ID)
                .nombre("Test Product")
                .idCategoria(CATEGORY_ID)
                .precioVenta(100.0)
                .cantidadStock(10)
                .estado(true)
                .build();

        productDomain = Product.builder()
                .productId(PRODUCT_ID)
                .name("Test Product")
                .categoryId(CATEGORY_ID)
                .price(100.0)
                .stock(10)
                .active(true)
                .build();
    }

    @Test
    void getAll_ShouldReturnAllProducts() {
        // Arrange
        List<Producto> productos = Arrays.asList(productoEntity);
        when(productoCrudRepository.findAll()).thenReturn(productos);

        // Act
        List<Product> result = productoRepository.getAll();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(productDomain.getProductId(), result.get(0).getProductId());
        assertEquals(productDomain.getName(), result.get(0).getName());
        verify(productoCrudRepository, times(1)).findAll();
    }

    @Test
    void getByCategory_WhenCategoryExists_ShouldReturnProducts() {
        // Arrange
        List<Producto> productos = Arrays.asList(productoEntity);
        when(productoCrudRepository.findByIdCategoriaOrderByNombreAsc(CATEGORY_ID))
                .thenReturn(productos);

        // Act
        Optional<List<Product>> result = productoRepository.getByCategory(CATEGORY_ID);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().isEmpty());
        Product actualProduct = result.get().get(0);
        assertEquals(productDomain.getProductId(), actualProduct.getProductId());
        assertEquals(productDomain.getName(), actualProduct.getName());
        verify(productoCrudRepository, times(1)).findByIdCategoriaOrderByNombreAsc(CATEGORY_ID);
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productoCrudRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(productoEntity));

        // Act
        Optional<Product> result = productoRepository.getProduct(PRODUCT_ID);

        // Assert
        assertTrue(result.isPresent());
        Product actualProduct = result.get();
        assertEquals(productDomain.getProductId(), actualProduct.getProductId());
        assertEquals(productDomain.getName(), actualProduct.getName());
        verify(productoCrudRepository, times(1)).findById(PRODUCT_ID);
    }

    @Test
    void getProduct_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(productoCrudRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productoRepository.getProduct(PRODUCT_ID);

        // Assert
        assertTrue(result.isEmpty());
        verify(productoCrudRepository, times(1)).findById(PRODUCT_ID);
    }

    @Test
    void save_ShouldSaveAndReturnProduct() {
        // Arrange
        when(productoCrudRepository.save(any(Producto.class))).thenReturn(productoEntity);

        // Act
        Product result = productoRepository.save(productDomain);

        // Assert
        assertNotNull(result);
        assertEquals(productDomain.getProductId(), result.getProductId());
        assertEquals(productDomain.getName(), result.getName());
        verify(productoCrudRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void delete_ShouldDeleteProduct() {
        // Act
        productoRepository.delete(PRODUCT_ID);

        // Assert
        verify(productoCrudRepository, times(1)).deleteById(PRODUCT_ID);
    }
}
