package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
public class Products {
  @Id private String productId;

  private String name;
  private String img = "default_image.png";
  private Integer quantity;
  private Integer price;
  private Date expiration_date;
  private Integer sell;
  private String status;
  private Integer stock_price;

  @ManyToOne
  @JoinColumn(name = "supplier_id")
  @JsonIgnoreProperties("products")
  private Suppliers supplier;

  @ManyToOne
  @JoinColumn(name = "category_id")
  @JsonIgnoreProperties("product")
  private Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference(value = "product-orderDetail")
  private List<Order_details> orderDetails;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<StockImportDetails> stockImportDetail;

  public String getProduct_id() {
    return productId;
  }

  public void setProduct_id(String product_id) {
    this.productId = product_id;
  }

  public Date getExpirationDate() {
    return expiration_date;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expiration_date = expirationDate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public Integer getSell() {
    return sell;
  }

  public void setSell(Integer sell) {
    this.sell = sell;
  }

  public Integer getStock_price() {
    return stock_price;
  }

  public void setStock_price(Integer stock_price) {
    this.stock_price = stock_price;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public Date getExpiration_date() {
    return expiration_date;
  }

  public void setExpiration_date(Date expiration_date) {
    this.expiration_date = expiration_date;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Suppliers getSupplier() {
    return supplier;
  }

  public void setSupplier(Suppliers supplier) {
    this.supplier = supplier;
  }

  public List<Order_details> getOrder_detail() {
    return orderDetails;
  }

  public void setOrder_detail(List<Order_details> order_detail) {
    this.orderDetails = order_detail;
  }

  public List<StockImportDetails> getStockImportDetail() {
    return stockImportDetail;
  }

  public void setStockImportDetail(List<StockImportDetails> stockImportDetail) {
    this.stockImportDetail = stockImportDetail;
  }
}
