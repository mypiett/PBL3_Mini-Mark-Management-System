package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
public class Category {
    @Id
    private String categoryId;
    private String name;

    // @OneToMany(mappedBy = "category")
    // @JsonIgnoreProperties("category")
    // private List<Products> products;

@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
@JsonIgnore// ← ngắt vòng lặp
private List<Products> product;




    public String getCategory_id() { return categoryId; }
    public void setCategory_id(String categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Products> getProduct() { return product; }
    public void setProduct(List<Products> products) { this.product = products; }

}
