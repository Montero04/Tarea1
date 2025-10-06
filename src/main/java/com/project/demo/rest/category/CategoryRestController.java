package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/categories")
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoriasPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriasPage.getTotalPages());
        meta.setTotalElements(categoriasPage.getTotalElements());
        meta.setPageNumber(categoriasPage.getNumber() + 1);
        meta.setPageSize(categoriasPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categorias retrieved successfully",
                categoriasPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{categoriaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoriaById(@PathVariable Long categoriaId, HttpServletRequest request) {
        Optional<Category> foundCategoria = categoryRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Categoria retrieved successfully",
                    foundCategoria.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoriaId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createCategoria(@RequestBody Category category, HttpServletRequest request) {
        Category savedCategory = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse("Categoria created successfully",
                savedCategory, HttpStatus.CREATED, request);
    }

    @PutMapping("/{categoriaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategoria(@PathVariable Long categoriaId, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategoria = categoryRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            category.setId(foundCategoria.get().getId());
            categoryRepository.save(category);
            return new GlobalResponseHandler().handleResponse("Categoria updated successfully",
                    category, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + categoriaId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PatchMapping("/{categoriaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> patchCategoria(@PathVariable Long categoriaId, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategoria = categoryRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            if(category.getName() != null) foundCategoria.get().setName(category.getName());
            if(category.getDescription() != null) foundCategoria.get().setDescription(category.getDescription());
            categoryRepository.save(foundCategoria.get());
            return new GlobalResponseHandler().handleResponse("Categoria updated successfully",
                    foundCategoria.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + categoriaId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{categoriaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long categoriaId, HttpServletRequest request) {
        Optional<Category> foundCategoria = categoryRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            categoryRepository.deleteById(categoriaId);
            return new GlobalResponseHandler().handleResponse("Category deleted successfully",
                    foundCategoria.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoriaId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}