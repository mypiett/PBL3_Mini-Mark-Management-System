package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock_import_details")
public class StockImportDetails {
    @Id
    private String importDetailId;

    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;

@ManyToOne
@JoinColumn(name = "productId")
@JsonBackReference(value = "product-stockDetail")
private Products product;


@ManyToOne
@JoinColumn(name = "importId")
@JsonBackReference(value = "import-stockDetail") // NGĂN vòng lặp StockImport -> StockImportDetail -> StockImport
private StockImport stockImport;

  // Getter and Setter for importDetailId
    public String getImportDetailId() {
        return importDetailId;
    }

    public void setImportDetailId(String importDetailId) {
        this.importDetailId = importDetailId;
    }

    // Getter and Setter for quantity
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for unitPrice
    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Getter and Setter for totalPrice
    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Getter and Setter for product
    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    // Getter and Setter for stockImport
    public StockImport getStockImport() {
        return stockImport;
    }

    public void setStockImport(StockImport stockImport) {
        this.stockImport = stockImport;
    }
}