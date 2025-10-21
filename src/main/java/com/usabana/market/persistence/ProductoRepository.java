package com.usabana.market.persistence;

import com.usabana.market.domain.Category;
import com.usabana.market.domain.Product;
import com.usabana.market.domain.repository.ProductRepository;
import com.usabana.market.persistence.crud.ProductoCrudRepository;
import com.usabana.market.persistence.entity.Producto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ProductoRepository implements ProductRepository {

    private final ProductoCrudRepository productoCrudRepository;


    @Override
    public List<Product> getAll() {
        return ((List<Producto>) productoCrudRepository.findAll()).stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Product>> getByCategory(int categoryId) {
        List<Producto> productos = productoCrudRepository.findByIdCategoriaOrderByNombreAsc(categoryId);
        List<Product> products = productos.stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
        return Optional.of(products);
    }

    @Override
    public Optional<Product> getProduct(int productId) {
        return productoCrudRepository.findById(productId)
                .map(this::mapToProduct);
    }

    @Override
    public Product save(Product product) {
        Producto producto = mapToProducto(product);
        Producto savedProducto = productoCrudRepository.save(producto);
        return mapToProduct(savedProducto);
    }

    private Product mapToProduct(Producto producto) {
        return Product.builder()
                .productId(producto.getIdProducto())
                .name(producto.getNombre())
                .categoryId(producto.getIdCategoria())
                .price(producto.getPrecioVenta())
                .stock(producto.getCantidadStock())
                .active(producto.getEstado())
                .category(producto.getCategoria() != null ?
                        Category.builder()
                                .categoryId(producto.getCategoria().getIdCategoria())
                                .category(producto.getCategoria().getDescripcion())
                                .active(producto.getCategoria().getEstado())
                                .build()
                        : null)
                .build();
    }

    private Producto mapToProducto(Product product) {
        return Producto.builder()
                .idProducto(product.getProductId())
                .nombre(product.getName())
                .idCategoria(product.getCategoryId())
                .precioVenta(product.getPrice())
                .cantidadStock(product.getStock())
                .estado(product.isActive())
                .build();
    }

    @Override
    public void delete(int productId) {
        productoCrudRepository.deleteById(productId);
    }
}