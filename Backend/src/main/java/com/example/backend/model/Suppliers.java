package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Suppliers {
  @Id private String supplierId;
  private String name;

  @Column(unique = true)
  private String phone;

  private String address;

  @Column(unique = true)
  private Long ind;

  @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<Products> products;

  public String getSupplier_id() {
    return supplierId;
  }

  public void setSupplier_id(String supplier_id) {
    this.supplierId = supplier_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getInd() {
    return ind;
  }

  public void setInd(Long ind) {
    this.ind = ind;
  }

  public List<Products> getProducts() {
    return products;
  }

  public void setProduct(List<Products> products) {
    this.products = products;
  }
}
