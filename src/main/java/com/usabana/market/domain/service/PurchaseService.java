package com.usabana.market.domain.service;

import com.usabana.market.domain.Purchase;
import com.usabana.market.domain.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public List<Purchase> getAll() {
        return purchaseRepository.getAll();
    }

    public Optional<List<Purchase>> getByClient(String clientId) {
        return purchaseRepository.getByClient(clientId);
    }

    public Purchase save(Purchase purchase) {
        if (purchase.getDate() == null) {
            purchase.setDate(LocalDateTime.now());
        }
        return purchaseRepository.save(purchase);
    }
}