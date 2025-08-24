package com.example.backend.model;

import java.sql.Date;
import jakarta.persistence.*;
import java.util.List;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Products {
    @Id
    private String productId;

    private String name;
    private String img = "default_image.png";
    private Integer quantity;
    private Integer price;
    private Date expiration_date;
    private Integer sell;
    private String status;
    private Integer stock_price;

    // @ManyToOne
    // @JoinColumn(name = "categoryId")
    // @JsonIgnoreProperties("products")
    // private Category category;

    // @ManyToOne
    // @JoinColumn(name = "supplier_id")
    // @JsonIgnoreProperties("products")
    // private Suppliers supplier;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @JsonIgnoreProperties("products")
    private Suppliers supplier;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("product") // ← để tránh vòng lặp ở Category
    private Category category;

    // @OneToMany(mappedBy = "product")
    // private List<Order_details> orderDetails;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @JsonManagedReference(value = "product-order")
    @JsonManagedReference(value = "product-orderDetail")
    private List<Order_details> orderDetails;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @JsonManagedReference(value = "product-stockDetail")
    private List<StockImportDetails> stockImportDetail;

    // Getter and Setter for product_id
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

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for img
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

    // Getter and Setter for quantity
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for price
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    // Getter and Setter for expiration_date
    public Date getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(Date expiration_date) {
        this.expiration_date = expiration_date;
    }

    // Getter and Setter for category
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Getter and Setter for supplier
    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    // Getter and Setter for order_detail
    public List<Order_details> getOrder_detail() {
        return orderDetails;
    }

    public void setOrder_detail(List<Order_details> order_detail) {
        this.orderDetails = order_detail;
    }

    // Getter and Setter for stockImportDetail
    public List<StockImportDetails> getStockImportDetail() {
        return stockImportDetail;
    }

    public void setStockImportDetail(List<StockImportDetails> stockImportDetail) {
        this.stockImportDetail = stockImportDetail;
    }
}