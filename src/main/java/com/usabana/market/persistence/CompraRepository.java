package com.usabana.market.persistence;

import com.usabana.market.domain.Purchase;
import com.usabana.market.domain.repository.PurchaseRepository;
import com.usabana.market.persistence.crud.CompraCrudRepository;
import com.usabana.market.persistence.entity.Compra;
import com.usabana.market.persistence.mapper.PurchaseMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CompraRepository implements PurchaseRepository {

    private final CompraCrudRepository compraCrudRepository;

    private final PurchaseMapper mapper;

    @Override
    public List<Purchase> getAll() {
        return mapper.toPurchases((List<Compra>) compraCrudRepository.findAll());
    }

    @Override
    public Optional<List<Purchase>> getByClient(String clientId) {
        return compraCrudRepository.findByIdCliente(clientId)
                .map(compras -> mapper.toPurchases(compras));
    }

    @Override
    public Purchase save(Purchase purchase) {
        Compra compra = mapper.toCompra(purchase);
        compra.getProductos().forEach(producto -> producto.setCompra(compra));

        return mapper.toPurchase(compraCrudRepository.save(compra));
    }
}
