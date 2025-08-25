package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock_import_details")
public class StockImportDetails {
  @Id private String importDetailId;

  private Integer quantity;
  private Integer unitPrice;
  private Integer totalPrice;

  @ManyToOne
  @JoinColumn(name = "productId")
  @JsonBackReference(value = "product-stockDetail")
  private Products product;

  @ManyToOne
  @JoinColumn(name = "importId")
  @JsonBackReference(value = "import-stockDetail")
  private StockImport stockImport;

  public String getImportDetailId() {
    return importDetailId;
  }

  public void setImportDetailId(String importDetailId) {
    this.importDetailId = importDetailId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(Integer unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Integer getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Integer totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Products getProduct() {
    return product;
  }

  public void setProduct(Products product) {
    this.product = product;
  }

  public StockImport getStockImport() {
    return stockImport;
  }

  public void setStockImport(StockImport stockImport) {
    this.stockImport = stockImport;
  }
}
