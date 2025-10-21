package com.usabana.market.persistence;

import com.usabana.market.domain.Purchase;
import com.usabana.market.domain.PurchaseItem;
import com.usabana.market.persistence.crud.CompraCrudRepository;
import com.usabana.market.persistence.entity.Compra;
import com.usabana.market.persistence.entity.ComprasProducto;
import com.usabana.market.persistence.entity.ComprasProductoPK;
import com.usabana.market.persistence.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraRepositoryTest {

    @Mock
    private CompraCrudRepository compraCrudRepository;

    private CompraRepository compraRepository;

    @BeforeEach
    void init() {
        compraRepository = new CompraRepository(compraCrudRepository);
    }

    private Compra compra;
    private Purchase purchase;
    private final String CLIENT_ID = "CLI-123";
    private final Integer COMPRA_ID = 1;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // Create product
        Producto producto = Producto.builder()
                .idProducto(1)
                .cantidadStock(5)
                .build();

        // Create ComprasProductoPK
        ComprasProductoPK comprasProductoPK = new ComprasProductoPK();
        comprasProductoPK.setIdCompra(COMPRA_ID);
        comprasProductoPK.setIdProducto(producto.getIdProducto());

        // Create purchase item with the composite key
        ComprasProducto compraProducto = new ComprasProducto();
        compraProducto.setId(comprasProductoPK);
        compraProducto.setCantidad(1);
        compraProducto.setTotal(100.0);
        compraProducto.setEstado(true);
        compraProducto.setProducto(producto);

        // Create purchase with items
        compra = Compra.builder()
                .idCompra(COMPRA_ID)
                .idCliente(CLIENT_ID)
                .fecha(now)
                .medioPago("Credit Card")
                .comentario("Test purchase")
                .estado("Completed")
                .productos(new java.util.ArrayList<>())
                .build();

        // Add the compraProducto to the compra's productos list
        compra.getProductos().add(compraProducto);
        // Set the compra reference in the compraProducto
        compraProducto.setCompra(compra);

        // Create purchase item for domain object
        PurchaseItem purchaseItem = PurchaseItem.builder()
                .productId(producto.getIdProducto())
                .quantity(compraProducto.getCantidad())
                .total(compraProducto.getTotal())
                .active(compraProducto.getEstado())
                .build();

        purchase = Purchase.builder()
                .purchaseId(COMPRA_ID)
                .clientId(CLIENT_ID)
                .date(now)
                .paymentMethod("Credit Card")
                .comment("Test purchase")
                .state("Completed")
                .items(List.of(purchaseItem))
                .build();
    }

    @Test
    void getAll_ShouldReturnListOfPurchases() {
        // Arrange
        List<Compra> compras = List.of(compra);
        when(compraCrudRepository.findAll()).thenReturn(compras);

        // Act
        List<Purchase> result = compraRepository.getAll();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "Should return one purchase");
        assertEquals(purchase.getPurchaseId(), result.get(0).getPurchaseId(), "Purchase ID should match");
        assertEquals(purchase.getClientId(), result.get(0).getClientId(), "Client ID should match");
        verify(compraCrudRepository, times(1)).findAll();
    }

    @Test
    void getByClient_WhenClientExists_ShouldReturnPurchases() {
        // Arrange
        List<Compra> compras = List.of(compra);
        when(compraCrudRepository.findByIdCliente(CLIENT_ID)).thenReturn(Optional.of(compras));

        // Act
        Optional<List<Purchase>> result = compraRepository.getByClient(CLIENT_ID);

        // Assert
        assertTrue(result.isPresent(), "Result should be present");
        assertFalse(result.get().isEmpty(), "Result should not be empty");
        assertEquals(purchase.getPurchaseId(), result.get().get(0).getPurchaseId(), "Purchase ID should match");
        assertEquals(purchase.getClientId(), result.get().get(0).getClientId(), "Client ID should match");
        verify(compraCrudRepository, times(1)).findByIdCliente(CLIENT_ID);
    }

    @Test
    void getByClient_WhenClientDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        String nonExistentClient = "NON_EXISTENT";
        when(compraCrudRepository.findByIdCliente(nonExistentClient)).thenReturn(Optional.empty());

        // Act
        Optional<List<Purchase>> result = compraRepository.getByClient(nonExistentClient);

        // Assert
        assertTrue(result.isEmpty(), "Result should be empty for non-existent client");
        verify(compraCrudRepository, times(1)).findByIdCliente(nonExistentClient);
    }

    @Test
    void save_ShouldSaveAndReturnPurchase() {
        // Arrange
        Producto producto = Producto.builder()
                .idProducto(2) // Using a different ID to avoid conflicts
                .cantidadStock(10)
                .build();

        // Create ComprasProductoPK
        ComprasProductoPK comprasProductoPK = new ComprasProductoPK();
        comprasProductoPK.setIdCompra(COMPRA_ID + 1); // Different ID to avoid conflicts
        comprasProductoPK.setIdProducto(producto.getIdProducto());

        // Create ComprasProducto with the composite key
        ComprasProducto compraProducto = new ComprasProducto();
        compraProducto.setId(comprasProductoPK);
        compraProducto.setCantidad(2);
        compraProducto.setTotal(200.0);
        compraProducto.setEstado(true);
        compraProducto.setProducto(producto);

        // Create a new compra for this test
        Compra testCompra = Compra.builder()
                .idCompra(COMPRA_ID + 1) // Different ID to avoid conflicts
                .idCliente(CLIENT_ID + "_test")
                .fecha(LocalDateTime.now())
                .medioPago("Credit Card")
                .comentario("Test purchase for save")
                .estado("Completed")
                .productos(new java.util.ArrayList<>())
                .build();

        // Add the compraProducto to the compra's productos list
        testCompra.getProductos().add(compraProducto);
        // Set the compra reference in the compraProducto
        compraProducto.setCompra(testCompra);

        // Create a test purchase for the save operation
        Purchase testPurchase = Purchase.builder()
                .purchaseId(testCompra.getIdCompra())
                .clientId(testCompra.getIdCliente())
                .date(testCompra.getFecha())
                .paymentMethod(testCompra.getMedioPago())
                .comment(testCompra.getComentario())
                .state(testCompra.getEstado())
                .items(testCompra.getProductos().stream()
                        .map(p -> PurchaseItem.builder()
                                .productId(p.getProducto().getIdProducto())
                                .quantity(p.getCantidad())
                                .total(p.getTotal())
                                .active(p.getEstado())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        when(compraCrudRepository.save(any(Compra.class))).thenReturn(testCompra);

        // Act
        Purchase result = compraRepository.save(testPurchase);

        // Assert
        assertNotNull(result, "Saved purchase should not be null");
        assertEquals(testPurchase.getPurchaseId(), result.getPurchaseId(), "Purchase ID should match");
        assertEquals(testPurchase.getClientId(), result.getClientId(), "Client ID should match");
        verify(compraCrudRepository, times(1)).save(any(Compra.class));

        // Verify the relationship
        assertFalse(testCompra.getProductos().isEmpty(), "La compra debe tener al menos un producto");
        ComprasProducto savedCompraProducto = testCompra.getProductos().get(0);
        assertNotNull(savedCompraProducto, "El producto de compra no debe ser nulo");
        assertNotNull(savedCompraProducto.getId(), "El ID del producto de compra no debe ser nulo");
        assertEquals(testCompra, savedCompraProducto.getCompra(), "La compra debe estar asociada al producto");
    }
}
