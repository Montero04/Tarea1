package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Products retrieved successfully",
                productsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductById(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if(foundProduct.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody Product product, HttpServletRequest request) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            return new GlobalResponseHandler().handleResponse("Category is required",
                    HttpStatus.BAD_REQUEST, request);
        }

        Optional<Category> foundCategory = categoryRepository.findById(product.getCategory().getId());
        if(foundCategory.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Category id " + product.getCategory().getId() + " not found",
                    HttpStatus.NOT_FOUND, request);
        }

        product.setCategory(foundCategory.get());
        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse("Product created successfully",
                savedProduct, HttpStatus.CREATED, request);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if(foundProduct.isPresent()) {
            product.setId(foundProduct.get().getId());

            if (product.getCategory() != null && product.getCategory().getId() != null) {
                Optional<Category> foundCategory = categoryRepository.findById(product.getCategory().getId());
                if(foundCategory.isEmpty()) {
                    return new GlobalResponseHandler().handleResponse("Category id " + product.getCategory().getId() + " not found",
                            HttpStatus.NOT_FOUND, request);
                }
                product.setCategory(foundCategory.get());
            } else {
                product.setCategory(foundProduct.get().getCategory());
            }

            productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    product, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> patchProduct(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if(foundProduct.isPresent()) {
            if(product.getName() != null) foundProduct.get().setName(product.getName());
            if(product.getDescription() != null) foundProduct.get().setDescription(product.getDescription());
            if(product.getPrice() != null) foundProduct.get().setPrice(product.getPrice());
            if(product.getQuantity() != null) foundProduct.get().setQuantity(product.getQuantity());

            productRepository.save(foundProduct.get());
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(productId);
        if(foundProduct.isPresent()) {
            productRepository.deleteById(productId);
            return new GlobalResponseHandler().handleResponse("Product deleted successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}