package com.usabana.market.domain.repository;

import com.usabana.market.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getAll();

    Optional<List<Product>> getByCategory(int categoryId);

    Optional<Product> getProduct(int productId);

    Product save(Product product);

    void delete(int productId);
}
