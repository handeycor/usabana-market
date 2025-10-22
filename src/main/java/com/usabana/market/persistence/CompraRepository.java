package com.usabana.market.persistence;

import com.usabana.market.domain.Purchase;
import com.usabana.market.domain.PurchaseItem;
import com.usabana.market.domain.repository.PurchaseRepository;
import com.usabana.market.persistence.crud.CompraCrudRepository;
import com.usabana.market.persistence.entity.Compra;
import com.usabana.market.persistence.entity.ComprasProducto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CompraRepository implements PurchaseRepository {

    private final CompraCrudRepository compraCrudRepository;

    @Override
    public List<Purchase> getAll() {
        return ((List<Compra>) compraCrudRepository.findAll()).stream()
                .map(this::toPurchase)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Purchase>> getByClient(String clientId) {
        return compraCrudRepository.findByIdCliente(clientId)
                .map(compras -> compras.stream()
                        .map(this::toPurchase)
                        .collect(Collectors.toList()));
    }

    @Override
    public Purchase save(Purchase purchase) {
        Compra compra = toCompra(purchase);
        compra.getProductos().forEach(product -> product.setCompra(compra));
        Compra savedCompra = compraCrudRepository.save(compra);
        return toPurchase(savedCompra);
    }

    private Purchase toPurchase(Compra compra) {
        return Purchase.builder()
                .purchaseId(compra.getIdCompra())
                .clientId(compra.getIdCliente())
                .date(compra.getFecha())
                .paymentMethod(compra.getMedioPago())
                .comment(compra.getComentario())
                .state(compra.getEstado())
                .items(compra.getProductos() != null ? compra.getProductos().stream()
                        .map(this::toPurchaseItem)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private PurchaseItem toPurchaseItem(ComprasProducto producto) {
        return PurchaseItem.builder()
                .productId(producto.getId().getIdProducto())
                .quantity(producto.getCantidad())
                .total(producto.getTotal())
                .active(producto.getEstado())
                .build();
    }

    private Compra toCompra(Purchase purchase) {
        Compra compra = Compra.builder()
                .idCompra(purchase.getPurchaseId())
                .idCliente(purchase.getClientId())
                .fecha(purchase.getDate())
                .medioPago(purchase.getPaymentMethod())
                .comentario(purchase.getComment())
                .estado(purchase.getState())
                .build();

        if (purchase.getItems() != null) {
            List<ComprasProducto> productos = purchase.getItems().stream()
                    .map(item -> {
                        ComprasProducto producto = new ComprasProducto();
                        producto.setCompra(compra);
                        producto.setCantidad(item.getQuantity());
                        producto.setTotal(item.getTotal());
                        producto.setEstado(item.isActive());
                        return producto;
                    })
                    .collect(Collectors.toList());
            compra.setProductos(productos);
        }

        return compra;
    }
}
