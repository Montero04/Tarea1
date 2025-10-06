package com.project.demo.logic.entity.category;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.logic.entity.product.Product;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "categoria")
@Entity
public class Category {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String name;
private String description;



    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descripcion) {
        this.description = descripcion;
    }


}
