package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {
  @Id private String categoryId;
  private String name;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<Products> product;

  public String getCategory_id() {
    return categoryId;
  }

  public void setCategory_id(String categoryId) {
    this.categoryId = categoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Products> getProduct() {
    return product;
  }

  public void setProduct(List<Products> products) {
    this.product = products;
  }
}
