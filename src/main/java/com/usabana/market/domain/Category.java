package com.usabana.market.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {
    private int categoryId;
    private String category;
    private boolean active;
}