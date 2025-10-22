package com.usabana.market.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseItem {
    private int productId;
    private int quantity;
    private double total;
    private boolean active;
}
