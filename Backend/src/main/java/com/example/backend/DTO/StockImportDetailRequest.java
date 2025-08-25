package com.example.backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockImportDetailRequest {
  @JsonProperty("importDetailId")
  private String importDetailId;

  private Integer quantity;

  @JsonProperty("unitPrice")
  private Integer unitPrice;

  @JsonProperty("totalPrice")
  private Integer totalPrice;

  @JsonProperty("product_id")
  private String productId;

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

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }
}
