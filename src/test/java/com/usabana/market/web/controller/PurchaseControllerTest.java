package com.usabana.market.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usabana.market.config.WebMvcTestConfig;
import com.usabana.market.domain.Purchase;
import com.usabana.market.domain.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PurchaseController.class, WebMvcTestConfig.class})
@AutoConfigureMockMvc
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseController purchaseController;

    @Autowired
    private ObjectMapper objectMapper;

    private Purchase purchase;
    private List<Purchase> purchaseList;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        purchase = Purchase.builder()
                .purchaseId(1)
                .clientId("client123")
                .date(now)
                .paymentMethod("CREDIT_CARD")
                .comment("Test purchase")
                .state("COMPLETED")
                .build();

        Purchase anotherPurchase = Purchase.builder()
                .purchaseId(2)
                .clientId("client123")
                .date(now.plusDays(1))
                .paymentMethod("CASH")
                .comment("Another test purchase")
                .state("PENDING")
                .build();

        purchaseList = Arrays.asList(purchase, anotherPurchase);
    }

    @Test
    void getAll_ShouldReturnListOfPurchases() throws Exception {
        // Given
        when(purchaseService.getAll()).thenReturn(purchaseList);

        // When/Then
        mockMvc.perform(get("/purchases/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].purchaseId", is(1)))
                .andExpect(jsonPath("$[0].clientId", is("client123")))
                .andExpect(jsonPath("$[1].purchaseId", is(2)));

        verify(purchaseService, times(1)).getAll();
    }

    @Test
    void getByClient_WhenClientExists_ShouldReturnPurchases() throws Exception {
        // Given
        String clientId = "client123";
        when(purchaseService.getByClient(clientId)).thenReturn(Optional.of(purchaseList));

        // When/Then
        mockMvc.perform(get("/purchases/client/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clientId", is(clientId)))
                .andExpect(jsonPath("$[1].clientId", is(clientId)));

        verify(purchaseService, times(1)).getByClient(clientId);
    }

    @Test
    void getByClient_WhenClientNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        String clientId = "nonExistingClient";
        when(purchaseService.getByClient(clientId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/purchases/client/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(purchaseService, times(1)).getByClient(clientId);
    }

    @Test
    void save_ShouldReturnCreatedPurchase() throws Exception {
        // Given
        when(purchaseService.save(any(Purchase.class))).thenReturn(purchase);

        // When/Then
        mockMvc.perform(post("/purchases/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.purchaseId", is(1)))
                .andExpect(jsonPath("$.clientId", is("client123")));

        verify(purchaseService, times(1)).save(any(Purchase.class));
    }
}