package com.usabana.market.domain.service;

import com.usabana.market.domain.Purchase;
import com.usabana.market.domain.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private Purchase purchase;
    private List<Purchase> purchaseList;
    private static final String CLIENT_ID = "client123";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        purchase = Purchase.builder()
                .purchaseId(1)
                .clientId(CLIENT_ID)
                .date(now)
                .paymentMethod("CREDIT_CARD")
                .comment("Test purchase")
                .state("COMPLETED")
                .build();

        Purchase anotherPurchase = Purchase.builder()
                .purchaseId(2)
                .clientId(CLIENT_ID)
                .date(now.minusDays(1))
                .paymentMethod("CASH")
                .comment("Another test purchase")
                .state("PENDING")
                .build();

        purchaseList = Arrays.asList(purchase, anotherPurchase);
    }

    @Test
    void getAll_ShouldReturnAllPurchases() {
        // Given
        when(purchaseRepository.getAll()).thenReturn(purchaseList);

        // When
        List<Purchase> result = purchaseService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(purchaseRepository, times(1)).getAll();
    }

    @Test
    void getByClient_WhenClientHasPurchases_ShouldReturnPurchases() {
        // Given
        when(purchaseRepository.getByClient(CLIENT_ID)).thenReturn(Optional.of(purchaseList));

        // When
        Optional<List<Purchase>> result = purchaseService.getByClient(CLIENT_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertTrue(result.get().stream().allMatch(p -> CLIENT_ID.equals(p.getClientId())));
        verify(purchaseRepository, times(1)).getByClient(CLIENT_ID);
    }

    @Test
    void getByClient_WhenClientHasNoPurchases_ShouldReturnEmpty() {
        // Given
        String nonExistentClientId = "nonExistentClient";
        when(purchaseRepository.getByClient(nonExistentClientId)).thenReturn(Optional.empty());

        // When
        Optional<List<Purchase>> result = purchaseService.getByClient(nonExistentClientId);

        // Then
        assertTrue(result.isEmpty());
        verify(purchaseRepository, times(1)).getByClient(nonExistentClientId);
    }

    @Test
    void save_ShouldReturnSavedPurchase() {
        // Given
        when(purchaseRepository.save(purchase)).thenReturn(purchase);

        // When
        Purchase result = purchaseService.save(purchase);

        // Then
        assertNotNull(result);
        assertEquals(purchase.getPurchaseId(), result.getPurchaseId());
        assertEquals(purchase.getClientId(), result.getClientId());
        verify(purchaseRepository, times(1)).save(purchase);
    }

    @Test
    void save_ShouldSetPurchaseDateIfNotProvided() {
        // Given
        Purchase newPurchase = Purchase.builder()
                .clientId(CLIENT_ID)
                .paymentMethod("CASH")
                .state("PENDING")
                .build();

        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase savedPurchase = invocation.getArgument(0);
            return Purchase.builder()
                    .purchaseId(1)
                    .clientId(savedPurchase.getClientId())
                    .date(savedPurchase.getDate())
                    .paymentMethod(savedPurchase.getPaymentMethod())
                    .state(savedPurchase.getState())
                    .build();
        });

        // When
        Purchase result = purchaseService.save(newPurchase);

        // Then
        assertNotNull(result.getDate());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }
}
