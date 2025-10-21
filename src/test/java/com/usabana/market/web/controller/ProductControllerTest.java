package com.usabana.market.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usabana.market.config.WebMvcTestConfig;
import com.usabana.market.domain.Product;
import com.usabana.market.domain.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ProductController.class, WebMvcTestConfig.class})
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductController productController;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productId(1)
                .name("Test Product")
                .categoryId(1)
                .price(100.0)
                .stock(10)
                .active(true)
                .build();

        Product anotherProduct = Product.builder()
                .productId(2)
                .name("Another Product")
                .categoryId(1)
                .price(150.0)
                .stock(5)
                .active(true)
                .build();
        anotherProduct.setActive(true);

        productList = Arrays.asList(product, anotherProduct);
    }

    @Test
    void getAll_ShouldReturnListOfProducts() throws Exception {
        // Given
        when(productService.getAll()).thenReturn(productList);

        // When/Then
        mockMvc.perform(get("/products/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[1].productId", is(2)))
                .andExpect(jsonPath("$[1].name", is("Another Product")));

        verify(productService, times(1)).getAll();
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        int productId = 1;
        when(productService.getProduct(productId)).thenReturn(Optional.of(product));

        // When/Then
        mockMvc.perform(get("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(productId)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(100.0)));

        verify(productService, times(1)).getProduct(productId);
    }

    @Test
    void getProduct_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        int productId = 999;
        when(productService.getProduct(productId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(productId);
    }

    @Test
    void getByCategory_WhenCategoryExists_ShouldReturnProducts() throws Exception {
        // Given
        int categoryId = 1;
        when(productService.getByCategory(categoryId)).thenReturn(Optional.of(productList));

        // When/Then
        mockMvc.perform(get("/products/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId", is(categoryId)))
                .andExpect(jsonPath("$[1].categoryId", is(categoryId)));

        verify(productService, times(1)).getByCategory(categoryId);
    }

    @Test
    void getByCategory_WhenNoProducts_ShouldReturnNotFound() throws Exception {
        // Given
        int categoryId = 999;
        when(productService.getByCategory(categoryId)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/products/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getByCategory(categoryId);
    }

    @Test
    void save_ShouldReturnCreatedProduct() throws Exception {
        // Given
        when(productService.save(any(Product.class))).thenReturn(product);

        // When/Then
        mockMvc.perform(post("/products/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")));

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    void delete_WhenProductExists_ShouldReturnOk() throws Exception {
        // Given
        int productId = 1;
        when(productService.delete(productId)).thenReturn(true);

        // When/Then
        mockMvc.perform(delete("/products/delete/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).delete(productId);
    }

    @Test
    void delete_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        int productId = 999;
        when(productService.delete(productId)).thenReturn(false);

        // When/Then
        mockMvc.perform(delete("/products/delete/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).delete(productId);
    }
}